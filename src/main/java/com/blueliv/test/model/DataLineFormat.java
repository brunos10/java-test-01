/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blueliv.test.model;

import com.blueliv.test.exception.UnknownFormatLineException;

/**
 *
 * @author bruno
 */
public enum DataLineFormat {
    
    F1("F1",",", "^[0-9]{8}[A-Z]$"),
    F2("F2"," ; ","^[0-9]{8}-[A-Z]$");
    
    public final String name;
    public final String separator;
    public final String personIdRegex;
    
    private DataLineFormat(String name, String separator, String personIdRegex) {
        this.name = name;
	this.separator = separator;
	this.personIdRegex = personIdRegex;
    }
    
    public static DataLineFormat parse(String formatLine) throws UnknownFormatLineException{
	if("F1".equals(formatLine)){
	    return F1;
	}else if("F2".equals(formatLine)){
	    return F2;
	}else{
	    throw UnknownFormatLineException.build(formatLine);
	}
    }
        
}
