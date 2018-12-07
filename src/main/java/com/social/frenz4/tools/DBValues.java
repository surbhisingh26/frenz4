package com.social.frenz4.tools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import com.github.javafaker.Faker;
import com.social.frenz4.service.EmailService;
import com.social.frenz4.service.UserService;
import com.social.scframework.App.Email;

public class DBValues {
	public void addUser(){
		Faker faker = new Faker();
		for(int i=0;i<10000;i++){
		String name = faker.name().fullName();
		String firstName = faker.name().firstName();
		String middleName = " ";
		String lastName = faker.name().lastName();

		String streetAddress = faker.address().streetAddress();
		System.out.println(name);
		System.out.println(firstName);
		System.out.println(lastName);
		System.out.println(streetAddress);
		System.out.println("------------------------");
		}
	}
	public static void main(String args[]){
		DBValues value = new DBValues();
		value.addUser();
	}
}
