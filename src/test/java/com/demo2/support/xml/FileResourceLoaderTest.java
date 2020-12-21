/*
 * Created by 2020-06-25 14:57:04 
 */
package com.demo2.support.xml;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.demo2.support.xml.FileResourceLoader;

/**
 * @author fangang
 */
public class FileResourceLoaderTest {
	private FileResourceLoader loader = new FileResourceLoader();
	
	@Test
	public void testWithRelativePath() throws IOException {
		String path = "src/test/java/com/demo2/support/xml";
		loader.loadResource(is->assertNotNull(is), path);
		
	}
	
	//@Test
	public void testWithAbsolutedPath() throws IOException {
		String path = "C:\\Development\\demo\\demo-service2-support\\src\\test\\java\\com\\demo2\\support\\xml";
		loader.loadResource(is->assertNotNull(is), path);
	}
}
