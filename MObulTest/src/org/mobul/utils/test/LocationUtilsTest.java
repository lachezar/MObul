package org.mobul.utils.test;

import org.mobul.utils.LocationUtils;

import junit.framework.TestCase;

public class LocationUtilsTest extends TestCase {
	
	public void testFormatValue() {
		assertEquals("-122°5'2\"", LocationUtils.formatAsDegree(-122.084095));
		assertEquals("37°25'19\"", LocationUtils.formatAsDegree(37.422006));

	}	

}
