/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blueliv.test.model;

import com.blueliv.test.exception.InvalidDataLineException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author bruno
 */
public class DataLineTest {
    
    private static String[][] data = {
	{"F1,Erica Burns,BARCELONA,93654902Y","F1","Erica Burns","BARCELONA","93654902Y"},
	{"F2 ; Russell Pope ; CARTAGENA ; 69429384-C","F2","Russell Pope","CARTAGENA","69429384C"}
    };
    
    private static String[] dataCorrupt = {
	"F1,Erica Burns; BARCELONA,93654902-Y",
	"F2,Erica Burns; BARCELONA,93654902-Y",
	"F2A,Erica Burns; BARCELONA, 93654902-Y"
    };
    
    @Test
    public void testParser01() {
	
	Arrays.asList(data).forEach( d -> {
	    try {
		DataLine dataLine = DataLine.parse(d[0]);
		Assert.assertEquals(d[2], dataLine.getPersonCompleteName());
		Assert.assertEquals(d[3], dataLine.getCityName());
		Assert.assertEquals(d[4], dataLine.getPersonId());
			
	    } catch (InvalidDataLineException ex) {
		Logger.getLogger(DataLineTest.class.getName()).log(Level.SEVERE, null, ex);
	    }
	
	});
    }
    
    @Test
    public void testParser02() {
	Arrays.asList(dataCorrupt).forEach( d -> {
	    try {
		DataLine.parse(d);
		Assert.assertTrue("Format isn't valid: '"+ d +"'", false);	
	    } catch (InvalidDataLineException ex) {
	    }
	});
    }
    
}
