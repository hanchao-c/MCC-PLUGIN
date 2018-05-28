package org.x.generater;

import org.x.generater.xml.BeanGenerator;
import org.x.util.StringCaseUtil;

public class ContextObjectDataModel {

	private String targetPackage;
	private String objectName;
	private String lowObjectName;
	private String suffix;
	private String fileTargetPath;
	private boolean overwrite;
	
	public ContextObjectDataModel() {
	}
	
	public ContextObjectDataModel(String objectName, BeanGenerator beanGenerator) {
		this.objectName = objectName + beanGenerator.getSuffix();
		accept(beanGenerator);
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
	
	private void accept(BeanGenerator beanGenerator){
		this.setLowObjectName(StringCaseUtil.toLower(this.objectName));
		this.setTargetPackage(beanGenerator.getTargetPackage());
		this.setSuffix(beanGenerator.getSuffix());
		this.setFileTargetPath(beanGenerator.buildJavaFilePath(this.objectName));
		this.setOverwrite(beanGenerator.getOverwrite());
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

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	@Override
	public String toString() {
		return "ContextObjectDataModel [targetPackage=" + targetPackage + ", objectName=" + objectName
				+ ", lowObjectName=" + lowObjectName + ", suffix=" + suffix + ", fileTargetPath=" + fileTargetPath
				+ ", overwrite=" + overwrite + "]";
	}
	
	}
