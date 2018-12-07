package com.social.frenz4.app;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
	public class PropertiesApp {
	
	  public String getProperty(String property) {

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("/apps/frenz4.properties");

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			//System.out.println(prop.getProperty("templatePath"));
			
			

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		return prop.getProperty(property);

	  }
	
}

