package org.x.generater;

public enum GenerateResourceType {

	FTL_CONTROLLER("controller"),
	FTL_SERVICE("service"),
	FTL_SERVICE_IMPL("serviceImpl"),
	FTL_REPOSITORY("repository"),
	FTL_REPOSITORY_IMPL("repositoryImpl"),
	MOCK("mock"),
	XML_MYBATIS_GENERATOR,
	XML_GENERATOR,
	XSD_GENERATOR,
	MODEL("model"),
	MAPPER("mapper");
	
	private final String subject;
	
	private GenerateResourceType() {
		this.subject = null;
	}
	
	private GenerateResourceType(String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}
	
}
