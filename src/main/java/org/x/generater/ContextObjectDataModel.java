package org.x.generater;

import org.apache.commons.lang3.StringUtils;
import org.x.generater.xml.BeanGenerator;
import org.x.util.StringCaseUtil;

public class ContextObjectDataModel {

	private String targetPackage;
	private String objectName;
	private String lowObjectName;
	private String suffix;
	private String fileTargetPath;
	
	public ContextObjectDataModel() {
	}
	
	public ContextObjectDataModel(String objectName, BeanGenerator beanGenerator) {
		this.objectName = objectName + beanGenerator.getSuffix();
		accept(beanGenerator, null);
	}
	
	public ContextObjectDataModel(String objectName, BeanGenerator beanGenerator, String implSuffix) {
		String upper = StringCaseUtil.toUpper(implSuffix);
		this.objectName = objectName + beanGenerator.getSuffix() + upper;
		accept(beanGenerator, upper);
	}
	
	
	public String getTargetPackage() {
		return targetPackage;
	}

	public void setTargetPackage(String targetPackage) {
		this.targetPackage = targetPackage;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getLowObjectName() {
		return lowObjectName;
	}

	public void setLowObjectName(String lowObjectName) {
		this.lowObjectName = lowObjectName;
	}
	
	private void accept(BeanGenerator beanGenerator, String implSuffix){
		if(StringUtils.isBlank(StringUtils.trimToNull(implSuffix))){
			this.setLowObjectName(StringCaseUtil.toLower(this.objectName));
			this.setTargetPackage(beanGenerator.getTargetPackage());
			this.setSuffix(beanGenerator.getSuffix());
			this.setFileTargetPath(beanGenerator.buildJavaFilePath(this.objectName));
		}else{
			this.setLowObjectName(StringCaseUtil.toLower(this.objectName));
			this.setTargetPackage(beanGenerator.getTargetPackage() + "." + StringCaseUtil.toLower(implSuffix));
			this.setSuffix(beanGenerator.getSuffix() + StringCaseUtil.toUpper(implSuffix));
			this.setFileTargetPath(beanGenerator.buildJavaFilePath("impl", this.objectName));
		}
	}

	@Override
	public String toString() {
		return "ContextObjectDataModel [targetPackage=" + targetPackage + ", objectName=" + objectName
				+ ", lowObjectName=" + lowObjectName + "]";
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getFileTargetPath() {
		return fileTargetPath;
	}

	public void setFileTargetPath(String fileTargetPath) {
		this.fileTargetPath = fileTargetPath;
	}


}
