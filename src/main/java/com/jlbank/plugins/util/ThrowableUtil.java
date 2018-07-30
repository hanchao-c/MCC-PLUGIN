package com.jlbank.plugins.util;

import com.jlbank.plugins.generater.xml.MavenPluginGeneratorBuildingException;

public class ThrowableUtil {

	
	public static MavenPluginGeneratorBuildingException throwOf(String message){
		if(null == message){
			throw throwOf();
		}
		throw new MavenPluginGeneratorBuildingException(message);
	}
	
	public static  MavenPluginGeneratorBuildingException throwOf(){
		throw new MavenPluginGeneratorBuildingException();
	}
}
