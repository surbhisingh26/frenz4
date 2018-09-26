package com.social.frenz4.actions;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.social.frenz4.app.PropertiesApp;
import com.social.frenz4.app.RequestResponseUtility;
import com.social.frenz4.service.EmailService;
import com.social.frenz4.service.UserService;
import com.social.scframework.App.Utility;


public class AdminActions extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Utility utility = new Utility();
	ObjectId uid;
	PropertiesApp prop = new PropertiesApp();
	String templatePath = prop.getProperty("templatePath");
	//String templatePath = "D:/apps/apache-tomcat-8.5.5/webapps/ROOT/WEB-INF/templates/fancy-colorlib";
	Map<String, Object> hmap = new HashMap<String, Object>();
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AdminActions() {
		super();	
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AdminActions adminaction = new AdminActions();


		String path = request.getPathInfo();
		System.out.println("path "+ path);


		if(path==null||path.equals("/")){

			//hmap = utility.checkSession(request);
			utility.getHbs(response,"home",hmap,templatePath);
		}

		else{
			try {
				String pathname = path.replace("/", "");
				Method method = AdminActions.class.getDeclaredMethod(pathname,HttpServletRequest.class,HttpServletResponse.class);

				method.invoke(adminaction,request,response);
			} catch (Exception e) {

				e.printStackTrace();
			} 
		}
	}
	public void emaillist(HttpServletRequest request, HttpServletResponse response){
		try {
			
			RequestResponseUtility rrutility = new RequestResponseUtility();
			Map<String, Object> hmap  = new HashMap<String, Object>();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");		
			if(uid!=null){
				utility.getHbs(response, "emaillist", hmap,templatePath);

			}
			else{
				hmap.put("message","Please login First!!!");
				response.sendRedirect("login");
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void emailtable(HttpServletRequest request, HttpServletResponse response){
		try {
			Map<String, Object> hmap = new HashMap<String, Object>();

			EmailService emailservice = new EmailService();

			String Varlimit = request.getParameter("limit");
			String offset = request.getParameter("offset");
			String order = request.getParameter("order");
			String sort = request.getParameter("sort");
			int limit = Integer.parseInt(Varlimit);
			int skip = Integer.parseInt(offset);
			if(sort==null){
				sort = "date";
			}
			System.out.println("................sort " + sort );
			System.out.println("................order " + order );
			String ascending = "false";
			if (order!=null && order.equalsIgnoreCase("asc")) {
				ascending = "true";
			} else {
				ascending = "false";
			}
			System.out.println("Calling..........");
			hmap.putAll(emailservice.emailtable(limit,skip,ascending,sort));
			System.out.println(hmap);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(new Gson().toJson(hmap));

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void userlist(HttpServletRequest request, HttpServletResponse response){
		try {
			
			RequestResponseUtility rrutility = new RequestResponseUtility();
			Map<String, Object> hmap  = new HashMap<String, Object>();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");		
			if(uid!=null){
				utility.getHbs(response, "userlist", hmap,templatePath);
			}
			else{
				hmap.put("message","Please login First!!!");
				response.sendRedirect("login");
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void usertable(HttpServletRequest request, HttpServletResponse response){
		try {
			Map<String, Object> hmap = new HashMap<String, Object>();

			UserService userservice = new UserService();

			String Varlimit = request.getParameter("limit");
			String offset = request.getParameter("offset");
			String order = request.getParameter("order");
			String sort = request.getParameter("sort");
			int limit = Integer.parseInt(Varlimit);
			int skip = Integer.parseInt(offset);
			if(sort==null){
				sort = "date";
			}
			String ascending = "false";
			if (order!=null && order.equalsIgnoreCase("asc")) {
				ascending = "true";
			} else {
				ascending = "false";
			}

			hmap.putAll(userservice.usertable(limit,skip,ascending,sort));
			System.out.println(hmap);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(new Gson().toJson(hmap));

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void modifyemail(HttpServletRequest request, HttpServletResponse response){
		try {
			//Map<String, Object> hmap = new HashMap<String, Object>();

			EmailService emailservice = new EmailService();


			String id = request.getParameter("id");
			String email = request.getParameter("email");
			String subject = request.getParameter("subject");
			String purpose = request.getParameter("purpose");
			String from = request.getParameter("from");
			String date = request.getParameter("date");
			System.out.println("Date is.......... " + date);
			String status = request.getParameter("status");
			String view = request.getParameter("view");
			emailservice.updateEmail(id,email,subject,purpose,from,date,status,view);
			response.sendRedirect("emaillist");

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void deleteemail(HttpServletRequest request, HttpServletResponse response){
		try {
			//Map<String, Object> hmap = new HashMap<String, Object>();

			EmailService emailservice = new EmailService();


			String id = request.getParameter("id");
			System.out.println("Is in delete email....... " + id);
			emailservice.deleteeEmail(id);
			response.sendRedirect("emaillist");

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void deletemanyemail(HttpServletRequest request, HttpServletResponse response){
		try {


			EmailService emailservice = new EmailService();


			String ids = request.getParameter("ids");
			System.out.println("Id in delete email....... " + ids);
			String id[] = ids.split(",");
			System.out.println(id.length);
			for(int i=0; i<id.length; i++){
				System.out.println(id[i]);
				emailservice.deleteeEmail(id[i]);
			}
			response.sendRedirect("emaillist");

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void editemail(HttpServletRequest request, HttpServletResponse response){
		try {


			EmailService emailservice = new EmailService();

			String id = request.getParameter("pk");
			String field = request.getParameter("name");
			String change = request.getParameter("value");
			System.out.println("id is "+id);
			System.out.println("name "+field);
			System.out.println("value "+change);
			emailservice.editemail(id,field,change);


			//response.sendRedirect("emaillist");

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void modifyuser(HttpServletRequest request, HttpServletResponse response){
		try {
			//Map<String, Object> hmap = new HashMap<String, Object>();

			UserService userservice = new UserService();
			String id = request.getParameter("id");
			String name = request.getParameter("name");
			String email = request.getParameter("email");
			String lastLoggedIn = request.getParameter("lastLoggedIn");
			String country = request.getParameter("country");


			userservice.updateUser(id,name,email,lastLoggedIn,country);
			response.sendRedirect("userlist");

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void deleteuser(HttpServletRequest request, HttpServletResponse response){
		try {
			//Map<String, Object> hmap = new HashMap<String, Object>();

			UserService userservice = new UserService();


			String id = request.getParameter("id");
			System.out.println("Is in delete email....... " + id);
			userservice.deleteUser(id);
			response.sendRedirect("userlist");

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void deletemanyuser(HttpServletRequest request, HttpServletResponse response){
		try {


			UserService userservice = new UserService();
			String ids = request.getParameter("ids");
			System.out.println("Id in delete email....... " + ids);
			String id[] = ids.split(",");
			System.out.println(id.length);
			for(int i=0; i<id.length; i++){
				System.out.println(id[i]);
				userservice.deleteUser(id[i]);
			}
			response.sendRedirect("userlist");

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void edituser(HttpServletRequest request, HttpServletResponse response){
		try {


			UserService userservice = new UserService();

			String id = request.getParameter("pk");
			String field = request.getParameter("name");
			String change = request.getParameter("value");
			System.out.println("id is "+id);
			System.out.println("name "+field);
			System.out.println("value "+change);
			userservice.editUser(id,field,change);


			//response.sendRedirect("userlist");

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void sitesettings(HttpServletRequest request, HttpServletResponse response){
		try {

			RequestResponseUtility rrutility = new RequestResponseUtility();
			Map<String, Object> hmap  = new HashMap<String, Object>();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");		
			if(uid!=null){
				UserService userService = new UserService();
				hmap.putAll(userService.getLatestPoints());
				utility.getHbs(response, "site_settings", hmap,templatePath);

			}
			else{
				hmap.put("message","Please login First!!!");
				utility.getHbs(response,"login_page",hmap,templatePath);
			}

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void savesitesettings(HttpServletRequest request, HttpServletResponse response){
		try {

			String type = request.getParameter("type");
			String adminName = request.getParameter("adminName");
			String point = request.getParameter("point");
			UserService userService = new UserService();
			Date date = new Date();
			int points = Integer.parseInt(point);
			userService.saveSiteSettings(type,adminName,points,date);


		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void referrallist(HttpServletRequest request, HttpServletResponse response){
		try {

			RequestResponseUtility rrutility = new RequestResponseUtility();
			Map<String, Object> hmap  = new HashMap<String, Object>();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");	
			if(uid!=null){
				utility.getHbs(response, "referrallist", hmap,templatePath);

			}
			else{
				hmap.put("message","Please login First!!!");
				utility.getHbs(response,"login_page",hmap,templatePath);
			}
			

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void referraltable(HttpServletRequest request, HttpServletResponse response){
		try {
			Map<String, Object> hmap = new HashMap<String, Object>();

			UserService userservice = new UserService();

			String Varlimit = request.getParameter("limit");
			String offset = request.getParameter("offset");
			String order = request.getParameter("order");
			String sort = request.getParameter("sort");
			int limit = Integer.parseInt(Varlimit);
			int skip = Integer.parseInt(offset);
			if(sort==null){
				sort = "date";
			}
			String ascending = "false";
			if (order!=null && order.equalsIgnoreCase("asc")) {
				ascending = "true";
			} else {
				ascending = "false";
			}

			hmap.putAll(userservice.referraltable(limit,skip,ascending,sort));
			//System.out.println(hmap);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(new Gson().toJson(hmap));

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}

