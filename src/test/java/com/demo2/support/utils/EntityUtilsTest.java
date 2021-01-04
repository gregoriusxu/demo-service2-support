package com.demo2.support.utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;

public class EntityUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testIsEntity() {
		assertTrue(EntityUtils.isEntity(Product.class));
	}

	@Test
	public void testCreateEntity() {
		Map<String, String> json = null;
		Product entity = EntityUtils.createEntity(Product.class, json);
		assertNotNull(entity);
	}
	
	@Test
	public void testCreateEntityWithValues() {
		Map<String, String> json = new HashMap<>();
		json.put("id", "40001");
		json.put("name", "computor");
		json.put("supplierId", "20001");
		json.put("createDate", "2020-01-01");
		json.put("updateDate", "2020-01-01 00:00:00");
		Product entity = EntityUtils.createEntity(Product.class, json);
		Product excepted = new Product(new Long(40001), "computor", new Long(20001), 
				DateUtils.getDate("2020-01-01", "yyyy-MM-dd"), 
				DateUtils.getDate("2020-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
		assertThat(entity,equalTo(excepted));
	}
	
	@Test
	public void testSetValueToEntity() {
		Product entity = new Product();
		Product excepted = new Product(new Long(40001), "computor", new Long(20001), 
				DateUtils.getDate("2020-01-01", "yyyy-MM-dd"), 
				DateUtils.getDate("2020-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
		EntityUtils.setValueToEntity(entity, "id", "40001");
		EntityUtils.setValueToEntity(entity, "name", "computor");
		EntityUtils.setValueToEntity(entity, "supplierId", "20001");
		EntityUtils.setValueToEntity(entity, "createDate", "2020-01-01");
		EntityUtils.setValueToEntity(entity, "updateDate", "2020-01-01 00:00:00");
		assertThat(entity, equalTo(excepted));
	}
	
	@Test
	public void testGetValueFromEntity() {
		Product entity = new Product(new Long(40001), "computor", new Long(20001),
				DateUtils.getDate("2020-01-01", "yyyy-MM-dd"), 
				DateUtils.getDate("2020-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
		EntityUtils.getValueFromEntity(entity, "id");
		assertThat(EntityUtils.getValueFromEntity(entity, "id"), equalTo(new Long(40001)));
		assertThat(EntityUtils.getValueFromEntity(entity, "name"), equalTo("computor"));
		assertThat(EntityUtils.getValueFromEntity(entity, "supplierId"), equalTo(new Long(20001)));
		assertThat(EntityUtils.getValueFromEntity(entity, "createDate"), 
				equalTo(DateUtils.getDate("2020-01-01", "yyyy-MM-dd")));
		assertThat(EntityUtils.getValueFromEntity(entity, "updateDate"), 
				equalTo(DateUtils.getDate("2020-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss")));
	}
	
	@Test
	public void testSetEntityToEntity() {
		Product entity = new Product();
		Product excepted = new Product();
		excepted.setSupplier(new Supplier(new Long(20001), "Alibaba"));
		EntityUtils.setValueToEntity(entity, "supplier", "{id:20001, name:'Alibaba'}");
		assertThat(entity, equalTo(excepted));
	}
	
	@Test
	public void testSetListToEntity() {
		Supplier entity = new Supplier();
		Supplier excepted = new Supplier();
		List<Product> products = new ArrayList<>();
		products.add(new Product(new Long(40001), "cup"));
		products.add(new Product(new Long(40002), "glass"));
		excepted.setProducts(products);
		
		String productsStr = "{id:40001,name:'cup'},{id:40002,name:'glass'}";
		EntityUtils.setValueToEntity(entity, "products", productsStr);
		assertThat(entity, equalTo(excepted));
	}
}
