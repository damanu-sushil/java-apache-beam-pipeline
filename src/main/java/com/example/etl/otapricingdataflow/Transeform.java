package co.nz.otapricingdataflow;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.beam.sdk.io.FileIO.ReadableFile;
import org.apache.beam.sdk.transforms.DoFn;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.bigquery.model.TableRow;

public class Transeform extends DoFn<ReadableFile, TableRow> {

	private static final long serialVersionUID = 1L;
	private String tableName;

	public Transeform(String tableName) {
		this.tableName = tableName;
	}

	@ProcessElement
	public void processElement(ProcessContext context) {

		ReadableFile readableElement = context.element();

		try {
			String element = new String(readableElement.readFullyAsBytes(), StandardCharsets.UTF_8);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(element);
			
					int propertyId = jsonNode.get("propertyId").asInt();
					String url = jsonNode.get("url").asText();
					String city = jsonNode.get("city").asText(null);
					String state = jsonNode.get("state").asText(null);
					String country = jsonNode.get("country").asText(null);
					String propertyName = jsonNode.get("propertyName").asText();
//					String updatedBy = jsonNode.get("updateBy").asText();
//					String updateDate = jsonNode.get("updateDate").asText();
					
					
					String updateDate = jsonNode.get("updateDate").asText();
					String updatedBy = jsonNode.get("updateBy").asText();

		 
					JsonNode dateJson = jsonNode.get("dates");
					
			if (dateJson != null && dateJson.isArray()) {
				for (JsonNode specificJsonNode : dateJson) {
					String date = specificJsonNode.get("date").asText();
					String day = specificJsonNode.get("day").asText();
					JsonNode otaJson = specificJsonNode.get("otas");
					
					if (otaJson != null && otaJson.isArray()) {
						for (JsonNode specificOtaJson : otaJson) {	
							
							String name = specificOtaJson.get("name").asText().toLowerCase();
							
							if (name.equals(tableName)) {
								TableRow row = new TableRow();
								row.set("name", name);
								row.set("propertyName", propertyName);
								row.set("propertyId", propertyId);
								row.set("url", url);
								row.set("city", city);
								row.set("state", state);
								row.set("country", country);
								row.set("date", date);
								row.set("day", day);
								row.set("price",specificOtaJson.get("price").asDouble(0.0));
								
//							    DateTimeZone timeZone = DateTimeZone.forID("Asia/Kolkata");
//							    Instant currentTimestamp = Instant.now();
//							    DateTime currentTime = new DateTime(currentTimestamp.toEpochMilli(), timeZone);
//							    String updateDate = currentTime.toString("YYYY-MM-dd HH:mm:ss");
								
								
							        
								row.set("updateDate", updateDate);
								row.set("updateBy", updatedBy);
								

								if (specificOtaJson.has("rooms") && !specificOtaJson.get("rooms").isArray()) {
									row.set("rooms", specificOtaJson.get("rooms").toString());
								} else {
									row.set("rooms", null);
								}
								
								row.set("currency", specificOtaJson.get("currency").asText());
								context.output(row);
							  }
						   }
						}
				   }
			    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}