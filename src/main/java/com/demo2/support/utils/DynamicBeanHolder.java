package com.demo2.support.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.cglib.beans.BeanGenerator;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.demo2.support.entity.Entity;

@Component
public class DynamicBeanHolder {
	private final ConfigurableApplicationContext applicationContext;

	public DynamicBeanHolder(ConfigurableApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public Entity<?> generateObject(String name, Map<?, ?> properties, Object... args) {
		BeanGenerator generator = new BeanGenerator();
		generator.setSuperclass(Entity.class);

		Set<?> keySet = properties.keySet();
		for (Iterator<?> i = keySet.iterator(); i.hasNext();) {
			String key = (String) i.next();
			generator.addProperty(key, (Class<?>) properties.get(key));
		}

		Entity<?> entity = (Entity<?>) generator.create();
		return registerBean(name, entity.getClass(), args);
	}

	public Object getValue(Object obj, String property) {
		BeanMap beanMap = BeanMap.create(obj);
		return beanMap.get(property);
	}

	public void setValue(Object obj, String property, Object value) {
		BeanMap beanMap = BeanMap.create(obj);
		beanMap.put(property, value);
	}

	public <T> T registerBean(String name, Class<T> clazz, Object... args) {
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
		if (args.length > 0) {
			for (Object arg : args) {
				beanDefinitionBuilder.addConstructorArgValue(arg);
			}
		}
		BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();

		BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
		beanFactory.registerBeanDefinition(name, beanDefinition);
		return applicationContext.getBean(name, clazz);
	}
}
