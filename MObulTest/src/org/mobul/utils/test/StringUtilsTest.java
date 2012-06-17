package org.mobul.utils.test;

import java.util.Arrays;

import org.mobul.utils.StringUtils;

import junit.framework.TestCase;

public class StringUtilsTest extends TestCase {
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}
	
	public void testJoin() {
		
		String[] items = new String[] {"a", "bb", "ccc", "dddd"};
		String[] singleItem = new String[] {"a"};
		
		assertEquals("a, bb, ccc, dddd", StringUtils.join(Arrays.asList(items), ", "));
		
		assertEquals("a_bb_ccc_dddd", StringUtils.join(Arrays.asList(items), "_"));
		
		assertEquals("abbcccdddd", StringUtils.join(Arrays.asList(items), ""));
		
		assertEquals("anullbbnullcccnulldddd", StringUtils.join(Arrays.asList(items), null));
		
		assertEquals("a", StringUtils.join(Arrays.asList(singleItem), "_"));
		
		assertEquals("a", StringUtils.join(Arrays.asList(singleItem), ""));
		
		assertEquals("a", StringUtils.join(Arrays.asList(singleItem), null));
		
	}
	
	public void testFormatFileSize() {
		assertEquals("1 KB", StringUtils.formatFileSize(0));
		assertEquals("1 KB", StringUtils.formatFileSize(1000));
		assertEquals("501 KB", StringUtils.formatFileSize(500*1024+123));
		assertEquals("0.9 MB", StringUtils.formatFileSize(900*1024+123));
		assertEquals("1.0 MB", StringUtils.formatFileSize(1020*1024+123));
		assertEquals("1.0 MB", StringUtils.formatFileSize(1024*1024+123));
		assertEquals("123.0 MB", StringUtils.formatFileSize(123*1024*1024+123));

	}
	
	public void testIsEmpty() {
		assertTrue(StringUtils.isEmpty(""));
		assertTrue(StringUtils.isEmpty((String)null));
		assertTrue(StringUtils.isEmpty("   "));
		assertFalse(StringUtils.isEmpty(" b "));
		assertTrue(StringUtils.isEmpty("\t"));
		assertFalse(StringUtils.isEmpty("abc"));
	}
	
	public void testGetHex() {
		byte[] bytes = new byte[] {0x12, 0x23, 0x45, (byte) 0xaa, (byte) 0xac, 0x64, (byte) 0xff}; 
		assertEquals("122345aaac64ff", StringUtils.getHex(bytes));
	}
	
	
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

}
