package com.social.frenz4.tools;

import com.social.frenz4.app.PropertiesApp;

public class TestPropertyFile{

	public static void main(String args[]){
		PropertiesApp prop = new PropertiesApp();
		String templatePath = prop.getProperty("templatePath");
		System.out.println(templatePath.toString());
	}
}
