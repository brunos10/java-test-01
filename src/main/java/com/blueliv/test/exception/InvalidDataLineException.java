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
public class InvalidDataLineException extends Exception{
    
    private static final String MESSAGE = "Format error: '%s' doesn't match regex '%s'";
    
    public static InvalidDataLineException build(String line, DataLineFormat format){
	String msg = String.format(MESSAGE, line, format);
	return new InvalidDataLineException(msg);
    }
    
    private InvalidDataLineException(String msg){
	super(msg);
    }
}
