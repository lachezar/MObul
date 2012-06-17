package org.mobul.geocoder;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.location.Location;

public class Geocoder {
	
	private static final String MAPS_URL = "http://maps.google.com/maps/geo?q=%LATITUDE%,%LONGITUDE%&output=xml&oe=utf8&sensor=true_or_false&hl=en";
	
	public static class Address {
		public String city;
		public String state;
		public String country;
		public String countryCode;
		
		public Address(String city, String state, String country, String countryCode) {
			this.city = city;
			this.state = state;
			this.country = country;
			this.countryCode = countryCode;
		}
	}
	
	public static Address reverseGeocode(Location loc) {
	    //http://maps.google.com/maps/geo?q=40.714224,-73.961452&output=json&oe=utf8&sensor=true_or_false&key=your_api_key
		String cityName = null;
		String stateName = null;
		String countryName = null;
		String countryCodeName = null;
	    HttpURLConnection connection = null;
	    URL serverAddress = null;

	    try 
	    {
	        // build the URL using the latitude & longitude you want to lookup
	        // NOTE: I chose XML return format here but you can choose something else
	        serverAddress = new URL(getMapsUrl(loc));
	        //set up out communications stuff
	        connection = null;
		      
	        //Set up the initial connection
			connection = (HttpURLConnection)serverAddress.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setReadTimeout(10000);
		                  
			connection.connect();
		    
			try
			{
				InputStreamReader isr = new InputStreamReader(connection.getInputStream());
				InputSource source = new InputSource(isr);
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				XMLReader xr = parser.getXMLReader();
				GoogleReverseGeocodeXmlHandler handler = new GoogleReverseGeocodeXmlHandler();
				
				xr.setContentHandler(handler);
				xr.parse(source);
				
				cityName = handler.getLocalityName();
				stateName = handler.getStateName();
				countryName = handler.getCountryName();
				countryCodeName = handler.getCountryCodeName();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			
	    }
	    catch (Exception ex)
	    {
	        ex.printStackTrace();
	    }
	    
	    return new Address(cityName, stateName, countryName, countryCodeName);
	}

	private static String getMapsUrl(Location loc) {
		return MAPS_URL.replace("%LATITUDE%", Double.toString(loc.getLatitude()))
						.replace("%LONGITUDE%", Double.toString(loc.getLongitude()));
						
	}
}
