package org.x.generater.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Table {

	@JacksonXmlProperty(isAttribute = true)
	private String tableName;
	@JacksonXmlProperty(isAttribute = true)
	private String domainObjectName;
	@JacksonXmlProperty(isAttribute = true)
	private String rootClass;
	

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getDomainObjectName() {
		return domainObjectName;
	}

	public void setDomainObjectName(String domainObjectName) {
		this.domainObjectName = domainObjectName;
	}

	public String getRootClass() {
		return rootClass;
	}

	public void setRootClass(String rootClass) {
		this.rootClass = rootClass;
	}

	@Override
	public String toString() {
		return "Table [tableName=" + tableName + ", domainObjectName=" + domainObjectName + ", rootClass=" + rootClass
				+ "]";
	}

}
