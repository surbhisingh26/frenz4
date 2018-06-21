package com.social.beFriendly.tools;



import org.apache.commons.lang3.RandomStringUtils;

public class RandomString {
public static void main(String args[]){
	 
	   
	    String generatedString = RandomStringUtils.randomAlphanumeric(7);
	 
	    System.out.println(generatedString);
}
}
