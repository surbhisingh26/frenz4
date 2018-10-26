package com.social.frenz4.tools;

import javax.persistence.GeneratedValue;

import com.social.frenz4.app.PropertiesApp;

public class JavaSyntex {
	
	public static void main(String[] args) {
		System.out.println(getValue(10));
		System.out.println(new PropertiesApp().getProperty("templatePath"));;
	}
	
	public static int getValue(int i) {
		System.out.println("d");

		return i;
	}
}
