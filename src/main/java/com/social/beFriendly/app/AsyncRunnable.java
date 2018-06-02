package com.social.beFriendly.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.social.beFriendly.service.UserService;



public class AsyncRunnable implements Runnable {
	 
	private final AsyncContext asyncContext;
	ObjectId recieverId;
    public AsyncRunnable(AsyncContext asyncContext,ObjectId recieverId) {
        this.asyncContext = asyncContext;
        this.recieverId = recieverId;
    }
 
    public void run() {
        HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
        Map<String,Object> hmap = new HashMap<String, Object>();
        try {
            System.out.println("About to sleep " + 5 + " seconds");
            System.out.println(recieverId);
            // faking a lengthy process
            Thread.sleep(5000L); // sleep 5 seconds
            
           UserService userService = new UserService();
           List<Object> chatList = userService.getmessage(recieverId);
           hmap.put("chatList", chatList);
          // hmap.put("date", new Date().toString());
            System.out.println("After calling complete");
            response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(new Gson().toJson(hmap));
			asyncContext.complete();
        } 
        catch (Exception e) {
            e.printStackTrace();
            
        }
    }
}
