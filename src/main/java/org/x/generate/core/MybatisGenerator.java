package org.x.generate.core;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class MybatisGenerator implements Rebuilder{

	private JdbcConnection jdbcConnection;
	@JacksonXmlProperty(localName = "model")
	private BaseBeanGenerator modelGenerator;
	@JacksonXmlProperty(localName = "mapping")
	private BaseBeanGenerator mappingGenerator;
	@JacksonXmlProperty(localName = "mapper")
	private BaseBeanGenerator mapperGenerator;
	
	@JacksonXmlProperty(isAttribute = true)
	private String targetPackage;
	@JacksonXmlProperty(isAttribute = true)
	private Boolean overwrite = Boolean.TRUE;

	public JdbcConnection getJdbcConnection() {
		return jdbcConnection;
	}

	public void setJdbcConnection(JdbcConnection jdbcConnection) {
		this.jdbcConnection = jdbcConnection;
	}

	public BaseBeanGenerator getModelGenerator() {
		return modelGenerator;
	}

	public void setModelGenerator(BaseBeanGenerator modelGenerator) {
		this.modelGenerator = modelGenerator;
	}

	public BaseBeanGenerator getMappingGenerator() {
		return mappingGenerator;
	}

	public void setMappingGenerator(BaseBeanGenerator mappingGenerator) {
		this.mappingGenerator = mappingGenerator;
	}

	public BaseBeanGenerator getMapperGenerator() {
		return mapperGenerator;
	}

	public void setMapperGenerator(BaseBeanGenerator mapperGenerator) {
		this.mapperGenerator = mapperGenerator;
	}

	@Override
	public String toString() {
		return "MybatisGenerator [jdbcConnection=" + jdbcConnection + ", modelGenerator=" + modelGenerator
				+ ", mappingGenerator=" + mappingGenerator + ", mapperGenerator=" + mapperGenerator + "]";
	}
	

	public static String exchangeCase(String str) {
	    String result = str.substring(0, 1).toUpperCase() + str.substring(1);
	    if(str.equals(result)){
	    	return toUpper(str);
	    }else{
	    	return toLower(str);
	    }
	}
	
	public static String toUpper(String str){
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	public static String toLower(String str){
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}

	@Override
	public void rebuild() {
		modelGenerator = rebuildGenerator(modelGenerator, "model");
		mappingGenerator = rebuildGenerator(mappingGenerator, "mapping");
		mapperGenerator = rebuildGenerator(mapperGenerator, "mapper");
	}

	private BaseBeanGenerator rebuildGenerator(BaseBeanGenerator generator, String packageSuffix) {
		if(null == generator){
			if(StringUtils.isBlank(this.getTargetPackage())){
				Throwables.throwOf("set attribute 'targetPackage' on tag <mybatisGenerator /> or it's sub tags <model />,<mapper />,<mapping />");
			}
			generator = new BaseBeanGenerator(getTargetPackage() + "." + packageSuffix);
		}else{
			if(StringUtils.isBlank(generator.getTargetPackage())){
				if(StringUtils.isBlank(this.getTargetPackage())){
					Throwables.throwOf("set attribute 'targetPackage' on tag <mybatisGenerator /> or it's sub tags <model />,<mapper />,<mapping />");
				}
				generator = new BaseBeanGenerator(this.getTargetPackage()+ "." + packageSuffix);
			}
		}
		return generator;
	}

	@Override
	public void validate() {
		
	}

	public String getTargetPackage() {
		return targetPackage;
	}

	public void setTargetPackage(String targetPackage) {
		this.targetPackage = targetPackage;
	}

	public Boolean getOverwrite() {
		return overwrite;
	}

	public void setOverwrite(Boolean overwrite) {
		this.overwrite = overwrite;
	}

}
