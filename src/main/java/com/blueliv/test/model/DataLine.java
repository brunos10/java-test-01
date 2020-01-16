/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blueliv.test.model;

import com.blueliv.test.exception.InvalidDataLineException;
import lombok.Data;


/**
 *
 * @author bruno
 */
@Data
public class DataLine {
    
    private final String personId;
    private final String personCompleteName;
    private final String cityName;
    
    public DataLine(String personCompleteName, String cityName, String personId){
	this.personId = personId;
	this.personCompleteName = personCompleteName;
	this.cityName = cityName;
    }
    
    /**
     * 
     * @param line in format 'F1,CARLOS PEREZ,SAN FRANCISCO,12345678Z' or 'F2 ; CARLOS PEREZ ; SAN FRANCISCO ; 12345678-Z'
     * @return DataLine object normalized. If personId is in F2 then, it's converted to F2 removing '-' character.
     * @throws InvalidDataLineException 
     */
    public static DataLine parse(String line) throws InvalidDataLineException{
	DataLineFormat format = DataLineFormat.F1;
	if(line.startsWith("F2")){
	    format = DataLineFormat.F2;
	}
	String[] dataLineSplitted = line.split(format.separator);
	if(dataLineSplitted.length != 4){
	    throw InvalidDataLineException.build(line, format);
	}
	if(!dataLineSplitted[3].matches(format.personIdRegex)){
	    throw InvalidDataLineException.build(line, format);
	}
	if(format.equals(DataLineFormat.F2)){
	    dataLineSplitted[3] = dataLineSplitted[3].replaceFirst("-", "");
	}
	return new DataLine(dataLineSplitted[1],
			    dataLineSplitted[2],
			    dataLineSplitted[3]);
	
    }
    
}
