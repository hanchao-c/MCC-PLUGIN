package org.x.generater.xml;

public class BeanGenerator{

	private String targetPackage;
	private String suffix;

	public BeanGenerator() {
	}
	
	public BeanGenerator(String targetPackage, String suffix) {
		this.targetPackage = targetPackage;
		this.suffix = suffix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getTargetPackage() {
		return targetPackage;
	}

	public void setTargetPackage(String targetPackage) {
		this.targetPackage = targetPackage;
	}

	
	public String buildJavaFilePath(String fileName){
		return "src/main/java/" + this.getTargetPackage().replace(".", "/") + "/" + fileName + ".java";
	}
	
	public String buildJavaFilePath(String prefix, String fileName){
		return "src/main/java/" + this.getTargetPackage().replace(".", "/") + "/" + prefix + "/" + fileName + ".java";
	}

	

}
