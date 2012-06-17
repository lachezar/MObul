package org.mobul.geocoder;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GoogleReverseGeocodeXmlHandler extends DefaultHandler 
{
	private boolean inLocalityName = false;
	private boolean inStateName = false;
	private boolean inCountryName = false;
	private boolean inCountryCodeName = false;
	private boolean finished = false;
	private StringBuilder builder;
	private String localityName = null;
	private String countryName = null;
	private String countryCodeName = null;
	private String stateName = null;

	public String getCountryCodeName() {
		return countryCodeName;
	}
	
	public String getCountryName() {
		return countryName;
	}

	public String getStateName() {
		return stateName;
	}
	
	public String getLocalityName()
	{
		return this.localityName;
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
	       throws SAXException {
	    super.characters(ch, start, length);
	    if ((this.inLocalityName || this.inStateName || this.inCountryName || this.inCountryCodeName) && !this.finished)
	    {
	    	if ((ch[start] != '\n') && (ch[start] != ' '))
	    	{
	    		builder.append(ch, start, length);
	    	}
	    }
	}

	@Override
	public void endElement(String uri, String localName, String name)
	        throws SAXException 
	{
	    super.endElement(uri, localName, name);
	    
	    if (!this.finished)
	    {
	    	if (localName.equalsIgnoreCase("LocalityName"))	{
	    		this.localityName = builder.toString();
	    	} else if (localName.equalsIgnoreCase("CountryName")) {
	    		this.countryName = builder.toString();
	    	} else if (localName.equalsIgnoreCase("CountryNameCode")) {
	    		this.countryCodeName = builder.toString();
	    	} else if (localName.equalsIgnoreCase("AdministrativeAreaName")) {
	    		this.stateName = builder.toString();
	    	}
	    	
	    	if (this.localityName != null && this.countryName != null && this.stateName != null) {
	    		this.finished = true;
	    	}
	    	
	    	if (builder != null) {
	    		builder.setLength(0);
	    	}
	    }
    }

    @Override
    public void startDocument() throws SAXException 
    {
        super.startDocument();
        builder = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
    {
    	super.startElement(uri, localName, name, attributes);
    	
   		this.inLocalityName = localName.equalsIgnoreCase("LocalityName");
   		this.inCountryName = localName.equalsIgnoreCase("CountryName");
   		this.inCountryCodeName = localName.equalsIgnoreCase("CountryNameCode");
   		this.inStateName = localName.equalsIgnoreCase("AdministrativeAreaName");
    }
}
