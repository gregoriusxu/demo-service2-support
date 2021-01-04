/*
 * Created by 2021-01-04 11:13:31 
 */
package com.demo2.support.utils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import com.demo2.support.entity.Entity;
import com.demo2.support.exception.OrmException;

/**
 * @author fangang
 */
public class EntityUtils {
	/**
	 * @param clazz
	 * @return whether the clazz is an entity
	 */
	public static boolean isEntity(Class<?> clazz) {
		if(Entity.class.isAssignableFrom(clazz)) return true;
		return false;
	}
	/**
	 * @param clazz
	 * @return
	 */
	public static boolean isListOfEntities(Class<?> clazz) {
		if(Collection.class.isAssignableFrom(clazz)) return false;
		return false;
	}
	/**
	 * create an entity and set values in it.
	 * @param clazz the class of the entity
	 * @param json the map of values
	 * @return the entity with values
	 */
	public static <T extends Entity<S>, S extends Serializable> T createEntity(Class<T> clazz, Map<String, String> json) {
		if(clazz==null) throw new OrmException("please give the class of the entity");
		try {
			T entity = clazz.newInstance();
			if(json!=null&&!json.isEmpty())
				for(String fieldName : json.keySet()) 
					setValueToEntity(entity, fieldName, json.get(fieldName));
			return entity;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new OrmException("error when create an entity: "+clazz, e);
		}
	}
	/**
	 * set the value to the field of the entity
	 * @param entity
	 * @param fieldName
	 */
	public static <S extends Serializable> void setValueToEntity(Entity<S> entity, String fieldName, Object value) {
		String firstStr = fieldName.substring(0,1);
		String setMethodName = "set"+firstStr.toUpperCase()+fieldName.substring(1);
		Method method = BeanUtils.getMethod(entity, setMethodName);
		Class<?>[] allOfParameterTypes = method.getParameterTypes();
		Class<?> firstOfParameterType = allOfParameterTypes[0];
		Object obj = BeanUtils.bind(firstOfParameterType, value);
		try {
			method.invoke(entity, obj);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new OrmException("error when invoke the method of an entity: "+setMethodName, e);
		} 
	}
	/**
	 * get the value of the field of the entity
	 * @param entity
	 * @param fieldName
	 * @return the value
	 */
	public static <S extends Serializable> Object getValueFromEntity(Entity<S> entity, String fieldName) {
		String firstStr = fieldName.substring(0,1);
		String getMethodName = "get"+firstStr.toUpperCase()+fieldName.substring(1);
		Method method = BeanUtils.getMethod(entity, getMethodName);
		try {
			return method.invoke(entity);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new OrmException("error when invoke the method of an entity: "+getMethodName, e);
		}
	}
}