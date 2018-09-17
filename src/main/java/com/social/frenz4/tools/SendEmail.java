package com.social.frenz4.tools;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;

import com.social.scframework.App.Email;
import com.social.scframework.App.Utility;

public class SendEmail {
	Utility utility = new Utility();
	public static void main(String args[]) throws ServletException, IOException{  
		//Get properties object  
		Email email = new Email();
		email.send("Surbhi", "surbhi.singh.ss05@gmail.com", "test", "invitationTemplate", "D:/apps/apache-tomcat-8.5.5/webapps/ROOT/WEB-INF/templates/fancy-colorlib/EmailTemplates", null);
		
		
		
		System.out.println("send email ");
		final String from = "surbhi.singh.ss05@gmail.com";
		final String password="as192118020809";
		System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
		Properties props = new Properties();    	
		props.put("mail.smtp.host", "smtp.gmail.com");    
		props.put("mail.smtp.socketFactory.class",    
				"javax.net.ssl.SSLSocketFactory");    
		props.put("mail.smtp.auth", "true");    
		props.put("mail.smtp.port", "465");    
		
		/*props.put("mail.smtp.user", from);
		props.put("mail.smtp.starttls.enable","true");
		props.put("mail.smtp.debug", "true");
		props.put("mail.smtp.socketFactory.fallback", "false");*/
		
		
		//get Session   
		
		Session session = Session.getDefaultInstance(props,    
				new javax.mail.Authenticator() {    
			protected PasswordAuthentication getPasswordAuthentication() {    
				return new PasswordAuthentication(from,password);  
			}    
		});    
		//compose message   
		
		try {    
			MimeMessage message = new MimeMessage(session);    
			message.addRecipient(Message.RecipientType.TO,new InternetAddress("surbhi.singh.ss05@gmail.com"));    
			message.setSubject("Test");   
			System.out.println("sending.....");
			//String text = utility.getHbsAsString(template,hashMap,templatePath);
			message.setText("Hello");
			//send message  
			System.out.println("transport");
//			Transport.send(message);    
			
			Transport transport = session.getTransport("smtps");
			transport.connect (props.get("mail.smtp.host").toString(), Integer.parseInt(props.get("mail.smtp.port").toString()), from, password);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();   
			
			
			System.out.println("message sent successfully");    
		}
		catch (MessagingException e) {e.printStackTrace();}    

	}  
}
