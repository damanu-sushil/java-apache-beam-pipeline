package co.nz.otapricingdataflow;

import org.apache.beam.sdk.transforms.DoFn;

import org.apache.beam.sdk.values.KV;


import com.google.api.services.bigquery.model.TableRow;

public class ExtractKeyFn extends DoFn<TableRow,KV<String,TableRow>> {
	private static final long serialVersionUID = 1L;

	@ProcessElement
	public void processElement(ProcessContext context) {

		TableRow row = context.element();
		  String key = String.format("%s_%s_%s_%s_%s_%s",
	                row.get("name"), row.get("propertyId"),row.get("propertyName"), row.get("url"), row.get("date"), row.get("day"));
	        context.output(KV.of(key, row));
	}
}
