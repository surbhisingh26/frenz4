package com.social.beFriendly.actions;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.social.beFriendly.app.Utility;


/**
 * Servlet implementation class UserActions
 */
public class UserActions extends HttpServlet {
	private static final long serialVersionUID = 1L;
       Utility utility = new Utility();
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
	
	
	}


