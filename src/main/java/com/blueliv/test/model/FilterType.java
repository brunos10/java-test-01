/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blueliv.test.model;

import com.blueliv.test.exception.InvalidFilterTypeException;

/**
 *
 * @author bruno
 */
public enum FilterType {

    ID,
    CITY;
    
    public static FilterType parse(String filterType) throws InvalidFilterTypeException{
	if("ID".equals(filterType)){
	    return ID;
	}else if("CITY".equals(filterType)){
	    return CITY;
	}else{
	    throw InvalidFilterTypeException.build(filterType);
	}
    }
    
}
