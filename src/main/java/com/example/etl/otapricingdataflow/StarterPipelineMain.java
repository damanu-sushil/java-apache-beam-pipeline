package co.nz.otapricingdataflow;

import java.io.IOException;
import java.util.List;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.Compression;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.io.FileIO.ReadableFile;
import org.apache.beam.sdk.io.fs.MatchResult;
import org.apache.beam.sdk.io.fs.MatchResult.Metadata;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.FileSystems;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.ValueProvider.StaticValueProvider;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.join.CoGroupByKey;
import org.apache.beam.sdk.transforms.join.KeyedPCollectionTuple;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TupleTag;

import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.TableId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StarterPipelineMain {

	private static final Logger LOG = LoggerFactory.getLogger(StarterPipelineMain.class);

	public static void main(String[] args) throws IOException {

		MyOption option = PipelineOptionsFactory.fromArgs(args).withValidation().create().as(MyOption.class);

		String projectId = GcpInfo.PROJECT_ID;
		String dataset = GcpInfo.DATASET;
		String tempPath = GcpInfo.TEMP_PATH;
		String inputFilePattern = GcpInfo.ALL_FILEPATH;
		String archiveFolder = GcpInfo.ARCHIEVE_FOLDER_PATH;
		String errorFolder = GcpInfo.ERROR_FOLDER_PATH;

		List<String> otas = GcpInfo.getOtas();
		StaticValueProvider<String> hotelPropertyTemp = StaticValueProvider.of(tempPath);

		// Enable FileSystems before matching
		FileSystems.setDefaultPipelineOptions(option);

		// Check for input files
		LOG.info("Checking for input files in: {}", inputFilePattern);
		MatchResult matchResult = FileSystems.match(inputFilePattern);

		if (matchResult.metadata().isEmpty()) {
			LOG.warn("No input files matched the pattern: {}", inputFilePattern);
			LOG.info("Exiting pipeline gracefully. No processing needed.");
			return;
		} else {
			LOG.info("Found {} input file(s) to process.", matchResult.metadata().size());
		}

		Pipeline pipeline = Pipeline.create(option);
		LOG.info("Pipeline initialized.");

		// Match and read all files
		PCollection<Metadata> matchedFiles = pipeline.apply("MatchAllFiles",
				FileIO.match().filepattern(inputFilePattern));
		LOG.info("Matching files with pattern: {}", inputFilePattern);

		PCollection<ReadableFile> readableFiles = matchedFiles
				.apply("ReadMatchedFiles", FileIO.readMatches().withCompression(Compression.AUTO));
		LOG.info("Reading matched files with auto compression.");

		// Filter, validate, and optionally archive or move to error
		PCollection<ReadableFile> filteredFiles = readableFiles
				.apply("ProcessFileFilter", ParDo.of(new ProcessFileFn(inputFilePattern, archiveFolder, errorFolder)));
		LOG.info("Processing files (filtering/archiving/error handling).");

		// Process OTA tables
		if (!otas.isEmpty()) {
			for (String tableName : otas) {
				LOG.info("Processing OTA table: {}", tableName);

				String fullyQualifiedTablePath = GcpInfo.DATASET_PATH + tableName;
				TableSchema tableSchema = SchemaGenerator.getSchema();

				BigQuery bigQuery = BigQueryOptions.newBuilder().setProjectId(projectId).build().getService();
				TableId tableId = TableId.of(projectId, dataset, tableName);
				Table table = bigQuery.getTable(tableId);

				PCollection<TableRow> transformedData = filteredFiles
						.apply("TransformData_" + tableName, ParDo.of(new Transeform(tableName)));

				if (table != null) {
					LOG.info("Table {} exists. Performing merge operation.", tableName);

					PCollection<KV<String, TableRow>> existingBQRows = pipeline
							.apply("ReadBQ_" + tableName, BigQueryIO.readTableRows().from(fullyQualifiedTablePath))
							.apply("ExtractKeyBQ_" + tableName, ParDo.of(new ExtractKeyFn()));

					PCollection<KV<String, TableRow>> transformedKeyed = transformedData
							.apply("ExtractKeyTransformed_" + tableName, ParDo.of(new ExtractKeyFn()));

					TupleTag<TableRow> transformedTag = new TupleTag<>("transformData");
					TupleTag<TableRow> bqTag = new TupleTag<>("bqRows");

					PCollection<TableRow> merged = KeyedPCollectionTuple
							.of(transformedTag, transformedKeyed)
							.and(bqTag, existingBQRows)
							.apply("CoGroupByKey_" + tableName, CoGroupByKey.create())
							.apply("CompareAndMerge_" + tableName,
									ParDo.of(new CompareAndMergeFn(transformedTag, bqTag)));

					merged.apply("WriteMerged_" + tableName,
							BigQueryIO.writeTableRows()
									.to(fullyQualifiedTablePath)
									.withSchema(tableSchema)
									.withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
									.withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_TRUNCATE)
									.withCustomGcsTempLocation(hotelPropertyTemp));
					LOG.info("Merged data written to table: {}", fullyQualifiedTablePath);

				} else {
					LOG.info("Table {} does not exist. Creating and appending data.", tableName);
					transformedData.apply("WriteNew_" + tableName,
							BigQueryIO.writeTableRows()
									.to(fullyQualifiedTablePath)
									.withSchema(tableSchema)
									.withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
									.withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND)
									.withCustomGcsTempLocation(hotelPropertyTemp));
					LOG.info("New data written to newly created table: {}", fullyQualifiedTablePath);
				}
			}
		} else {
			LOG.warn("No OTA table list found. Exiting pipeline.");
		}

		LOG.info("Starting pipeline...");
		pipeline.run();
		LOG.info("Pipeline finished successfully.");
	}
}
