package co.nz.otapricingdataflow;

import java.io.IOException;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.apache.beam.sdk.io.FileIO.ReadableFile;
import org.apache.beam.sdk.transforms.DoFn;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class ProcessFileFn extends DoFn<ReadableFile,ReadableFile> {
	
	private static final long serialVersionUID = 1L;
	private String inputFilePartten;
	private String archiveFolder;
	private String errorFolder ;
	

	public ProcessFileFn(String inputFilePartten,String archiveFolder, String errorFolder) {
			
			this.inputFilePartten = inputFilePartten;
			this.archiveFolder = archiveFolder;
			this.errorFolder = errorFolder;
			
	}

	@ProcessElement
	public void processElement(ProcessContext context) throws URISyntaxException {
	    Storage storage = StorageOptions.getDefaultInstance().getService();
		 
		ReadableFile readableElement = context.element();
		String fileName = readableElement.getMetadata().resourceId().getFilename();
		    int lastIndex = inputFilePartten.lastIndexOf('/');
	        int secondLastIndex = inputFilePartten.lastIndexOf('/', lastIndex - 1);
	        String folder = inputFilePartten.substring(secondLastIndex + 1, lastIndex + 1); 
            String filepath = folder + fileName;
 
		try {
			String element = new String(readableElement.readFullyAsBytes(), StandardCharsets.UTF_8);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(element);
			String otaName = null;

					jsonNode.get("url");
					jsonNode.get("city");
					jsonNode.get("state");
					jsonNode.get("country");
				    jsonNode.get("hotelName");
				    jsonNode.get("updateBy");
				    jsonNode.get("updateDate");
				    
					JsonNode dateJson = jsonNode.get("dates");
					
			if (dateJson != null && dateJson.isArray()) {
				for (JsonNode specificJsonNode : dateJson) {
					specificJsonNode.get("date");
					specificJsonNode.get("day");
					JsonNode otaJson = specificJsonNode.get("otas");
					
					if (otaJson != null && otaJson.isArray()) {
						for (JsonNode specificOtaJson : otaJson) {	
							otaName = specificOtaJson.get("name").asText().toLowerCase();
							specificOtaJson.get("price");
							specificOtaJson.get("rooms");
						   }
						}
				     }
			      }
					context.output(readableElement);
		            try {
		            	 String destinationPath = archiveFolder + fileName;
						 BlobInfo blobInfo = Blob.newBuilder(GcpInfo.BUCKET, destinationPath)
						    .setContentType("application/json")
						    .build();
						 byte[] readAllBytes = readableElement.readFullyAsBytes();
						 storage.create(blobInfo,readAllBytes);
						 
						 BlobId sourceBlobId = BlobId.of(GcpInfo.BUCKET, filepath);
						 storage.delete(sourceBlobId);
						 
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			
		} catch (Exception e) {
            try {
            	 String destinationPath = errorFolder + fileName;
				 BlobInfo blobInfo = Blob.newBuilder(GcpInfo.BUCKET, destinationPath)
				    .setContentType("application/json")
				    .build();
				 byte[] readAllBytes = readableElement.readFullyAsBytes();
				 storage.create(blobInfo,readAllBytes);
				 
				 BlobId sourceBlobId = BlobId.of(GcpInfo.BUCKET, filepath);
				 storage.delete(sourceBlobId);
				 
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
