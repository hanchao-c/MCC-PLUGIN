package org.x.generate.core;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Table {

	@JacksonXmlProperty(isAttribute = true)
	private String tableName;
	@JacksonXmlProperty(isAttribute = true)
	private String domainObjectName;

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

	@Override
	public String toString() {
		return "Table [tableName=" + tableName + ", domainObjectName=" + domainObjectName + "]";
	}

}
