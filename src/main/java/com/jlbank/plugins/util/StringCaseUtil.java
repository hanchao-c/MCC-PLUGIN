package com.jlbank.plugins.util;

public class StringCaseUtil {

	public static String toUpper(String str){
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	public static String toLower(String str){
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}
	
	
}
