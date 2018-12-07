package com.social.frenz4.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


	import junit.framework.TestCase;
	public class EmailTest extends TestCase {
	private int value1;
	private int value2;
	public EmailTest(String testName) {
	super(testName);
	}
	protected void setUp() throws Exception {
	super.setUp();
	value1 = 3;
	value2 = 5;
	}
	protected void tearDown() throws Exception {
	super.tearDown();
	value1 = 0;
	value2 = 0;
	}
	public void testAdd() {
	int total = 8;
	int sum = 2;
	assertEquals(sum, total);
	}
	public void testFailedAdd() {
	int total = 9;
	int sum = 4;
	assertNotSame(sum, total);
	}
	public void testSub() {
	int total = 0;
	int sub = 6;
	assertEquals(sub, total);
	}

}
