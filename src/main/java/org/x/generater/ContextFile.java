package org.x.generater;

public class ContextFile {

	private String filePath;
	private String context;
	private boolean overwirte;

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

	public boolean isOverwirte() {
		return overwirte;
	}

	public void setOverwirte(boolean overwirte) {
		this.overwirte = overwirte;
	}

	@Override
	public String toString() {
		return "ContextFile [filePath=" + filePath + ", context=" + context + ", overwirte=" + overwirte + "]";
	}
	
	
	

}
