package co.nz.otapricingdataflow;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.DoFn.ProcessContext;
import org.apache.beam.sdk.transforms.DoFn.ProcessElement;
import org.apache.beam.sdk.transforms.join.CoGbkResult;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TupleTag;
import org.checkerframework.checker.initialization.qual.Initialized;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.UnknownKeyFor;

import com.google.api.services.bigquery.model.TableRow;

public class CompareAndMergeFn extends DoFn<KV<String, CoGbkResult>, TableRow> {

	private static final long serialVersionUID = 1L;
	private final TupleTag<TableRow> transformDataTag;
	private final TupleTag<TableRow> bqRowsTag;

	public CompareAndMergeFn(TupleTag<TableRow> transformDataTag, TupleTag<TableRow> bqRowsTag) {
		this.transformDataTag = transformDataTag;
		this.bqRowsTag = bqRowsTag;
	}

	@ProcessElement
	public void processElement(ProcessContext context) {

		KV<String, CoGbkResult> element = context.element();
		Iterable<TableRow> transformDataRows = element.getValue().getAll(transformDataTag);
		Iterable<TableRow> bqRows = element.getValue().getAll(bqRowsTag);

		TableRow mergedRow = new TableRow();

		for (TableRow row : bqRows) {
			boolean match = false;
			for (TableRow rows : transformDataRows) {
				if (isEqualRow(row, rows)) {
					mergedRow.putAll(rows);
					match = true;
					break;
				}
			}

			if (!match) {
				mergedRow.putAll(row);
			}
		}

		for (TableRow row : transformDataRows) {
			boolean match = false;
			for (TableRow rows : bqRows) {
				if (isEqualRow(row, rows)) {
					match = true;
					break;
				}
			}
			if (!match) {
				mergedRow.putAll(row);
			}
		}

		context.output(mergedRow);
	}

	private boolean isEqualRow(TableRow row, TableRow rows) {
		return row.get("propertyId").equals(rows.get("propertyId"))
				&& row.get("propertyName").equals(rows.get("propertyName")) && rows.get("name").equals(rows.get("name"))
				&& rows.get("date").equals(rows.get("date")) && rows.get("url").equals(rows.get("url"))
				&& rows.get("day").equals(rows.get("day"));

	}
}
