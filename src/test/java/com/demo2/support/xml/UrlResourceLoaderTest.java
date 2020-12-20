/*
 * Created by 2020-06-25 16:05:46 
 */
package com.demo2.support.xml;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.demo2.support.xml.UrlResourceLoader;

/**
 * @author fangang
 */
public class UrlResourceLoaderTest {
	private UrlResourceLoader loader = new UrlResourceLoader();

	@Test
	public void testWithClassPath() throws IOException {
		String path = "classpath:vObj.xml";
		loader.loadResource(is->assertNotNull(is), path);
	}

}
