package org.x.generater.xml;

public class BeanGenerator{

	private String targetPackage;
	private String suffix;
	private String templatePath;
	private Boolean overwrite;

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
	
	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}
	
	public Boolean getOverwrite() {
		return overwrite;
	}

	public void setOverwrite(Boolean overwrite) {
		this.overwrite = overwrite;
	}
	
	
	public String buildJavaFilePath(String fileName){
		return "src/main/java/" + this.getTargetPackage().replace(".", "/") + "/" + fileName + ".java";
	}

	@Override
	public String toString() {
		return "BeanGenerator [targetPackage=" + targetPackage + ", suffix=" + suffix + ", templatePath=" + templatePath
				+ ", overwrite=" + overwrite + "]";
	}
	
	


}
