package co.nz.otapricingdataflow;

import java.util.ArrayList;
import java.util.List;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableSchema;

public class SchemaGenerator {
	public static TableSchema getSchema() {
		List<TableFieldSchema> fieldSchemas = new ArrayList<TableFieldSchema>();
		fieldSchemas.add(new TableFieldSchema().setName("name").setType("STRING"));
		fieldSchemas.add(new TableFieldSchema().setName("propertyName").setType("STRING"));
		fieldSchemas.add(new TableFieldSchema().setName("propertyId").setType("INTEGER"));
		fieldSchemas.add(new TableFieldSchema().setName("url").setType("STRING"));
		fieldSchemas.add(new TableFieldSchema().setName("city").setType("STRING"));
		fieldSchemas.add(new TableFieldSchema().setName("state").setType("STRING"));
		fieldSchemas.add(new TableFieldSchema().setName("country").setType("STRING"));
		fieldSchemas.add(new TableFieldSchema().setName("date").setType("DATE"));
		fieldSchemas.add(new TableFieldSchema().setName("day").setType("STRING"));
		fieldSchemas.add(new TableFieldSchema().setName("price").setType("FLOAT"));
		fieldSchemas.add(new TableFieldSchema().setName("currency").setType("STRING"));
		fieldSchemas.add(new TableFieldSchema().setName("rooms").setType("STRING"));
		
		
		fieldSchemas.add(new TableFieldSchema().setName("updateDate").setType("DATETIME"));
		fieldSchemas.add(new TableFieldSchema().setName("updateBy").setType("STRING"));
		fieldSchemas.add(new TableFieldSchema().setName("optimizeDate").setType("DATETIME"));
		fieldSchemas.add(new TableFieldSchema().setName("optimizeBy").setType("STRING"));

		return new TableSchema().setFields(fieldSchemas);
	}
}
