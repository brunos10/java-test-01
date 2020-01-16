/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blueliv.test.exception;

import com.blueliv.test.model.DataLineFormat;

/**
 *
 * @author bruno
 */
public class InvalidFilterTypeException extends Exception{
    
    private static final String MESSAGE = "Invalid filter type exception error: '%s'. It must be ID or 'CITY'";
    
    public static InvalidFilterTypeException build(String filterType){
	String msg = String.format(MESSAGE, filterType);
	return new InvalidFilterTypeException(msg);
    }
    
    private InvalidFilterTypeException(String msg){
	super(msg);
    }
}
