package co.nz.otapricingdataflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GcpInfo {
	public static final String PROJECT_ID = "impactful-name-306810";
	public static final String BUCKET = "thehotelmate_ota_pricing_data";
	public static final String DATASET = "ota_details";
	public static final String TEMP_PATH = "gs://thehotelmate_ota_pricing_data/temp";
	public static final String ALL_FILEPATH = "gs://thehotelmate_ota_pricing_data/input/*.json";
	public static final String ARCHIEVE_FOLDER_PATH = "archive/";
	public static final String ERROR_FOLDER_PATH = "errors/";
	public static final String DATASET_PATH = "impactful-name-306810.ota_details.";
	
	
	public static List<String> getOtas() {
		 List<String> otas = 
				 new ArrayList<>(Arrays.asList("thehotelmate","makemytrip","goibibo","trip_com","agoda","easemytrip","expedia",
						 "priceline","booking_com","bluepillow","yatra","hotels_com","cleartrip","trivago","oyo")); 
		return otas;
		
	}
}

