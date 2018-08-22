package com.social.frenz4.app;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.social.frenz4.service.UserService;



public class AsyncRunnable implements Runnable {
	 
	private final AsyncContext asyncContext;
	ObjectId uid;
    public AsyncRunnable(AsyncContext asyncContext,ObjectId recieverId) {
        this.asyncContext = asyncContext;
        this.uid = recieverId;
    }
 
    public void run() {
        HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
        Map<String,Object> hmap = new HashMap<String, Object>();
        try {
            //System.out.println("About to sleep " + 5 + " seconds");
           // System.out.println(recieverId);
            // faking a lengthy process
            Thread.sleep(5000L); // sleep 5 seconds
            
           UserService userService = new UserService();
           hmap.putAll(userService.getmessage(uid));
           hmap.putAll(userService.getMessageStatus(uid));
          // System.out.println("step1 run");
          
          // hmap.put("date", new Date().toString());
            //System.out.println("After calling complete");
            response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(new Gson().toJson(hmap));
			//System.out.println("step1 run");
			asyncContext.complete();
			//System.out.println("step1 run");
        } 
        catch (Exception e) {
            e.printStackTrace();
            
        }
    }
}
