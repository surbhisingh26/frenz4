package com.social.frenz4.tools;

import java.lang.reflect.Field;

import com.social.frenz4.model.User;

public class ReflectionTool {
public static void main(String args[]) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
	Class<User> myObjectClass = User.class;
	User user = new User();
	String className = "com.social.frenz4.model.User";
	try {
		Class<?>[] clas = Class.forName(className).getInterfaces();
		System.out.println(clas);
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	//Method[] methods = myObjectClass.getMethods();

	Field field   = user.getClass().getDeclaredField("points");
	
	//if (Modifier.isPrivate(field.getModifiers())){
		field.setAccessible(true);
    	System.out.println("name" + " : "+field.getName());
    	System.out.println("name" + " : "+field.getType().getName());
	//}
	/*System.out.println(methods);
	for(Method m:methods){
		System.out.println(m.getName());
	}*/
	System.out.println("Fields");
	/*for(Field m:fields){
		if (Modifier.isPrivate(m.getModifiers())) {
     	   m.setAccessible(true);
		System.out.println(m.getName());
		try {
			System.out.println(m.get(user));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
//}
}
}
