package org.x.generate.core;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Generator implements Rebuilder{
	
	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "property")
	private List<Property> properties;
	
	private ContextGenerator contextGenerator;
	private MybatisGenerator mybatisGenerator;

	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "table")
	private List<Table> tables;
	
	
	private boolean skippedContext = false;
	
	public ContextGenerator getContextGenerator() {
		return contextGenerator;
	}

	public void setContextGenerator(ContextGenerator contextGenerator) {
		this.contextGenerator = contextGenerator;
	}

	public MybatisGenerator getMybatisGenerator() {
		return mybatisGenerator;
	}

	public void setMybatisGenerator(MybatisGenerator mybatisGenerator) {
		this.mybatisGenerator = mybatisGenerator;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public List<Table> getTables() {
		return tables;
	}

	public void setTables(List<Table> tables) {
		this.tables = tables;
	}

	@Override
	public String toString() {
		return "Generator [properties=" + properties + ", contextGenerator=" + contextGenerator + ", mybatisGenerator="
				+ mybatisGenerator + ", tables=" + tables + "]";
	}


	@Override
	public void rebuild() {
		if(isEmpty(this.tables)){
			Throwables.throwOf("No definition is found of <table />");
		}
		if(null == this.contextGenerator){
			skippedContext = true;
		}else{
			contextGenerator.rebuild();
		}
		if(null == mybatisGenerator){
			Throwables.throwOf("none tag <mybatisGenerator /> set");
		}
		mybatisGenerator.rebuild();
		
		for (Table table : tables) {
			if(StringUtils.isAnyBlank(table.getDomainObjectName(), table.getTableName())){
				Throwables.throwOf("No values are set for attribute domainObjectName or tableName on tag <table />");
			}
			table.setDomainObjectName(toUpper(table.getDomainObjectName()));
		}
		
	}

	public static String toUpper(String str){
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	public static String toLower(String str){
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}
	
	public boolean skippedContextGenerator(){
		if(skippedContext){
			return true;
		}
		if(this.contextGenerator.getInUsing()){
			return false;
		}else{
			return true;
		}
		
	}
	
	
	private static boolean isEmpty(Collection<?> collection){
		return null == collection || collection.isEmpty();
	}

	@Override
	public void validate() {
		
	}

	
	/*private static boolean isEmpty(Map<?, ?> map){
		return null == map || map.isEmpty();
	}
	*/

}
