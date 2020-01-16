/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blueliv.test.exception;

/**
 *
 * @author bruno
 */
public class UnknownFormatLineException extends Exception{
    
    private static final String MESSAGE = "Unkown format error: '%s' doesn't exists. You have to use F1 or F2";
    
    public static UnknownFormatLineException build(String line){
	String msg = String.format(MESSAGE, line);
	return new UnknownFormatLineException(msg);
    }
    
    private UnknownFormatLineException(String msg){
	super(msg);
    }
}
