/*
 * Created by 2020-06-25 14:06:59 
 */
package com.demo2.support.xml;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.demo2.support.xml.ClassPathResourceLoader;

/**
 * @author fangang
 */
public class ClassPathResourceLoaderTest {

	@Test
	public void test() throws IOException {
		ClassPathResourceLoader loader = new ClassPathResourceLoader(ClassPathResourceLoaderTest.class);
		loader.loadResource(is->assertNotNull(is), "vObj.xml");
	}

}
