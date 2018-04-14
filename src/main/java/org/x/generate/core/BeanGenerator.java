package org.x.generate.core;

public class BeanGenerator extends BaseBeanGenerator{

	protected String suffix;

	public BeanGenerator() {
	}
	
	public BeanGenerator(String targetPackage, String suffix) {
		super(targetPackage);
		this.suffix = suffix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	
	

}
