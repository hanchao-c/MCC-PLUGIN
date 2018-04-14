package org.x.generate.core;

public class BaseBeanGenerator {

	private String targetPackage;
	
	public BaseBeanGenerator() {
	}

	public BaseBeanGenerator(String targetPackage) {
		super();
		this.targetPackage = targetPackage;
	}

	public String getTargetPackage() {
		return targetPackage;
	}

	public void setTargetPackage(String targetPackage) {
		this.targetPackage = targetPackage;
	}

	@Override
	public String toString() {
		return "BaseBeanGenerator [targetPackage=" + targetPackage + "]";
	}
	
	
	public String buildJavaFilePath(String fileName){
		return "src/main/java/" + this.getTargetPackage().replace(".", "/") + "/" + fileName + ".java";
	}
	
	public String buildJavaFilePath(String prefix, String fileName){
		return "src/main/java/" + this.getTargetPackage().replace(".", "/") + "/" + prefix + "/" + fileName + ".java";
	}

	
}
