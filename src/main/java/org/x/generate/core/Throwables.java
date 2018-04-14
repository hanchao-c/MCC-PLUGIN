package org.x.generate.core;

public class Throwables {

	
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
