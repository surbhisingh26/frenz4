package com.social.frenz4.tools;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bson.types.ObjectId;
import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;

import com.mongodb.BasicDBObject;
import com.social.frenz4.DAO.UserDAO;
import com.social.frenz4.model.User;
import com.social.scframework.App.Utility;

public class DbReflection {
	public static void main(String args[]) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, ClassNotFoundException, NoSuchFieldException{
		String db = "user";
		String dbClass =  db.substring(0, 1).toUpperCase().concat(db.substring(1));
		String dao  = dbClass.concat("DAO");
		String field = "name";
		String id = "5b9bb1bdd8448c1370fbd032";
		String newvalue = "Viv";
		System.out.println(dao);
		Class DAOclass = null;
		try {
			DAOclass = Class.forName("com.social.frenz4.DAO."+dao);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(DAOclass);
		Method method = null;

		method = DAOclass.getDeclaredMethod(db.substring(0, 1).toLowerCase().concat(db.substring(1)).concat("DAO"));


		JacksonDBCollection<Object, String> Collection = (JacksonDBCollection<Object, String>) method.invoke(DAOclass.newInstance());
		System.out.println(Collection);
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		DBCursor<Object> cursor = Collection.find(query);
		System.out.println(cursor.count());
		Class CollectionClass = Class.forName("com.social.frenz4.model."+dbClass);
		String fieldSetMethod = "set" + field.substring(0, 1).toUpperCase().concat(field.substring(1));
		Field fieldType = CollectionClass.getDeclaredField(field);
		Class<?> fieldTypeClass = fieldType.getType();
		Utility util = new Utility();
		Object Value = util.changeType(fieldTypeClass.getSimpleName(), newvalue);
		System.out.println("type :   " + fieldType.getType().getSimpleName());
		if(cursor.hasNext()){
			Object c = cursor.next();
			Method setMethod = CollectionClass.getDeclaredMethod(fieldSetMethod, fieldTypeClass);
			setMethod.invoke(c, Value);
			System.out.println("sasas");
			Collection.updateById(id, c);
		}

	}
}
