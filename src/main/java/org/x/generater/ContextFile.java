package org.x.generater;

public class ContextFile {

	private String filePath;
	private String context;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	@Override
	public String toString() {
		return "ContextFile [filePath=" + filePath + ", context=" + context + "]";
	}
	
	

}
