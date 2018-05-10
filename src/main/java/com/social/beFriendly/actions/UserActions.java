package com.social.beFriendly.actions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.social.beFriendly.app.Utility;
import com.social.beFriendly.service.UserService;



/**
 * Servlet implementation class UserActions
 */
public class UserActions extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Utility utility = new Utility();
	String uid;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserActions() {
		super();
		// TODO Auto-generated constructor stub
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
		UserActions useraction = new UserActions();


		String path = request.getPathInfo();
		System.out.println("path "+ path);


		if(path==null||path.equals("/")){
			Map<String, Object> hmap  = new HashMap<String, Object>();
			//hmap = utility.checkSession(request);
			utility.getHbs(response,"home",hmap);
		}

		else{
			try {
				String ur = path.replace("/", "");
				Method method = UserActions.class.getDeclaredMethod(ur,HttpServletRequest.class,HttpServletResponse.class);

				method.invoke(useraction,request,response);
			} catch (Exception e) {

				e.printStackTrace();
			} 
		}
	}

	public void login(HttpServletRequest request,HttpServletResponse response){

		try {
			utility.getHbs(response,"login_page",null);
		} catch (ServletException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void register(HttpServletRequest request,HttpServletResponse response){

		try {
			utility.getHbs(response,"register_page",null);
		} catch (ServletException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	public void adduser(HttpServletRequest request,HttpServletResponse response){

		try {
			String fname = request.getParameter("fname");
			String mname = request.getParameter("mname");
			String lname = request.getParameter("lname");
			String email = request.getParameter("email");
			String country = request.getParameter("country");
			String city = request.getParameter("city");
			String password = request.getParameter("pass");
			String dob = request.getParameter("dob");
			String mobile =request.getParameter("mobile");


			String reference = request.getParameter("reference");
			if(reference==null)
				reference = "No reference";
			String referenceId = request.getParameter("referenceId");
			if(referenceId==null)
				referenceId="null";
			String gender = request.getParameter("gender");
			String bgcolor = "#000000";
			String rootPath = System.getProperty("catalina.home");
			String savePath = rootPath + File.separator + "webapps/images/beFriendlyimages";
			File fileSaveDir=new File(savePath);
			//File file = new File(rootPath + File.separator + "images");
			//File fileSaveDir=new File(file);
			if(!fileSaveDir.exists()){
				fileSaveDir.mkdir();
			}
			//String imagePath= fileSaveDir + File.separator + "default.jpg";
			String filePath=null;
			if(reference.equals("fb")){
				filePath = request.getParameter("picUrl");
			}
			else
				filePath = File.separator +"images/beFriendlyimages" + File.separator + "default.jpg";

			UserService rs = new UserService();
			Boolean result = rs.registerUser(fname, lname, mname,country,city,mobile,password,gender,dob,bgcolor,filePath,email,reference,referenceId);
			Map<String, Object> hmap  = new HashMap<String, Object>();
			if(result == false){

				String msg = "You are already registered with this email";
				hmap.put("message", msg);
				//utility.getHbs(response,"message",hmap);
				utility.getHbs(response,"register_page",hmap);
			}
			else{
				String registeredMsg = "You are successfully registered!!! Login to Continue";
				hmap.put("message", registeredMsg);


				if(!referenceId.equals("null")){
					response.sendRedirect("/beFriendly");
				}
				else
				{					
					utility.getHbs(response,"login_page",hmap);
				}

			}
		} catch (ServletException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void signin(HttpServletRequest request,HttpServletResponse response){

		try {
			String email = request.getParameter("email");
			System.out.println("username "+email);
			if(email==null)
				email="";
			String password = request.getParameter("pass");
			System.out.println("password "+password);
			if(password==null)
				password="";
			String reference = request.getParameter("reference");
			if(reference==null)
				reference = "No reference";
			String referenceId = request.getParameter("referenceId");
			if(referenceId==null)
				referenceId="null";
			System.out.println("reference is "+reference);
			System.out.println("ref Id is "+referenceId);
			UserService userservice = new UserService();
			String result = userservice.checkValid(email,password,reference,referenceId);
			Map<String, Object> hmap  = new HashMap<String, Object>();
			//System.out.println(request.getParameter("firstname"));
			System.out.println(result);
			String msg = null;
			if(result.equals(email)){
				msg = "No such username exists!!!"
						+ " Register or login with another username";

				hmap.put("message", msg);

				utility.getHbs(response,"login_page",hmap);

			}
			else if(result.equals(password)){
				msg = "Wrong password entered";
				hmap.put("message", msg);
				utility.getHbs(response,"login_page",hmap);
			}

			else if(result.equals("Register First")){
				//hmap.put("register", true);
				//utility.getHbs(response,"registration",hmap);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(new Gson().toJson(hmap));


			}
			else {

				Cookie loginCookie = new Cookie("uid",result);

				System.out.println("uid is "+result);
				response.addCookie(loginCookie);
				loginCookie.setMaxAge(30*60); 			
				//UserService userservice = new UserService();
				Boolean loggedIn = userservice.login(result);
				hmap.put("LoggedIn", loggedIn);

				//hmap.put("register", false);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(new Gson().toJson(hmap));
				if(referenceId.equals("null")){

					response.sendRedirect("dashboard");
				}

			}


		} catch (ServletException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void dashboard(HttpServletRequest request,HttpServletResponse response){

		try {
			Map<String, Object> hmap  = new HashMap<String, Object>();
			hmap = utility.checkSession(request);
			utility.getHbs(response,"dashboard",hmap);
		} catch (ServletException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	public void profile(HttpServletRequest request,HttpServletResponse response){

		try {
			Map<String, Object> hmap  = new HashMap<String, Object>();
			hmap = utility.checkSession(request);
			utility.getHbs(response,"profile",hmap);
		} catch (ServletException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	public void logout(HttpServletRequest request,HttpServletResponse response){
		try{
			Map<String, Object> hmap = new HashMap<String, Object>();
			hmap = utility.checkSession(request);
			uid = (String) hmap.get("uid");
			String reference = request.getParameter("reference");



			Cookie loginCookie=new Cookie("uid","");  
			loginCookie.setMaxAge(0);  
			response.addCookie(loginCookie); 

			UserService userservice = new UserService();
			userservice.logout(uid);

			//System.out.println(request.getContextPath());
			//request.getRequestDispatcher("").forward(request, response);
			if(reference==null)
				response.sendRedirect("/beFriendly");
			return;
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void gallery(HttpServletRequest request,HttpServletResponse response){
		try{
			Map<String, Object> hmap = new HashMap<String, Object>();
			hmap = utility.checkSession(request);
			uid = (String) hmap.get("uid");
			utility.getHbs(response,"picture_gallery",hmap);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void friends(HttpServletRequest request,HttpServletResponse response){
		try{
			Map<String, Object> hmap = new HashMap<String, Object>();
			hmap = utility.checkSession(request);
			uid = (String) hmap.get("uid");
			utility.getHbs(response,"friends",hmap);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}


