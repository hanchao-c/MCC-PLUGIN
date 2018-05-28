package org.x.generater.xml;

import org.apache.commons.lang3.StringUtils;
import org.x.util.StringCaseUtil;
import org.x.util.ThrowableUtil;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class MybatisGenerator implements Rebuilder{

	private JdbcConnection jdbcConnection;
	@JacksonXmlProperty(localName = "model")
	private BeanGenerator modelGenerator;
	@JacksonXmlProperty(localName = "mapping")
	private BeanGenerator mappingGenerator;
	@JacksonXmlProperty(localName = "mapper")
	private BeanGenerator mapperGenerator;
	
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

	public BeanGenerator getModelGenerator() {
		return modelGenerator;
	}

	public void setModelGenerator(BeanGenerator modelGenerator) {
		this.modelGenerator = modelGenerator;
	}

	public BeanGenerator getMappingGenerator() {
		return mappingGenerator;
	}

	public void setMappingGenerator(BeanGenerator mappingGenerator) {
		this.mappingGenerator = mappingGenerator;
	}

	public BeanGenerator getMapperGenerator() {
		return mapperGenerator;
	}

	public void setMapperGenerator(BeanGenerator mapperGenerator) {
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
		modelGenerator = rebuildGenerator(modelGenerator, "model", null);
		mappingGenerator = rebuildGenerator(mappingGenerator, "mapping", null);
		mapperGenerator = rebuildGenerator(mapperGenerator, "mapper", StringCaseUtil.toUpper("Mapper"));
	}

	private BeanGenerator rebuildGenerator(BeanGenerator generator, String packageSuffix, String suffix) {
		suffix = StringUtils.trimToEmpty(suffix);
		if(null == generator){
			if(StringUtils.isBlank(this.getTargetPackage())){
				ThrowableUtil.throwOf("set attribute 'targetPackage' on tag <mybatisGenerator /> or it's sub tags <model />,<mapper />,<mapping />");
			}
			generator = new BeanGenerator(getTargetPackage() + "." + packageSuffix, suffix);
		}else{
			if(StringUtils.isBlank(generator.getTargetPackage())){
				if(StringUtils.isBlank(this.getTargetPackage())){
					ThrowableUtil.throwOf("set attribute 'targetPackage' on tag <mybatisGenerator /> or it's sub tags <model />,<mapper />,<mapping />");
				}
				generator = new BeanGenerator(generator.getTargetPackage(), suffix);
			}
		}
		
		if(null == generator.getOverwrite()) {
			generator.setOverwrite(overwrite);
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
