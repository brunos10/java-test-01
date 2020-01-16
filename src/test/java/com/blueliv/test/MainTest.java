/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blueliv.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author bruno
 */
public class MainTest {

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUpStreams() {
	System.setOut(new PrintStream(out));
	System.setErr(new PrintStream(err));
    }

    @After
    public void restoreStreams() {
	System.setOut(originalOut);
	System.setErr(originalErr);
    }

    @Test
    public void test01() {

	String path = MainTest.class.getResource("/data.txt").getPath();
	String[] args = {path, "CITY", "CARTAGENA"};
	Main.main(args);
	String output = out.toString();
	Assert.assertEquals("Russell Pope,69429384C\n", output);
    }

    @Test
    public void test02() {
	String path = MainTest.class.getResource("/data.txt").getPath();
	String[] args = {path, "ID", "54808168L"};
	Main.main(args);
	String output = out.toString();
	output = output.substring(0, output.length() - 1);
	String[] cities = output.split("\n");
	List<String> citiesList = Arrays.asList(cities);
	Assert.assertEquals(3, cities.length);
	String[] expected = {"BARCELONA", "OVIEDO", "MADRID"};
	for (String city : expected) {
	    Assert.assertTrue("'" + city + "' isn't in cities", citiesList.contains(city));
	}
    }

    @Test
    public void test03() {
	String path = MainTest.class.getResource("/data_corrupt.txt").getPath();
	String[] args = {path, "ID", "54808168L"};
	Main.main(args);
    }

}
