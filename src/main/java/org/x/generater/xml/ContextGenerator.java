package org.x.generater.xml;

import org.apache.commons.lang3.StringUtils;
import org.x.util.StringCaseUtil;
import org.x.util.ThrowableUtil;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ContextGenerator implements Rebuilder{

	@JacksonXmlProperty(localName = "controller")
	private BeanGenerator controllerGenerator;
	@JacksonXmlProperty(localName = "service")
	private BeanGenerator serviceGenerator;
	@JacksonXmlProperty(localName = "repository")
	private BeanGenerator repositoryGenerator;

	@JacksonXmlProperty(isAttribute = true)
	private String targetPackage;
	@JacksonXmlProperty(isAttribute = true)
	private Boolean overwrite = Boolean.TRUE;
	@JacksonXmlProperty(isAttribute = true)
	private Boolean inUsing = Boolean.TRUE;
	
	@Override
	public void rebuild() {
		if(inUsing){
			controllerGenerator = rebuildGenerator(controllerGenerator, StringCaseUtil.toUpper("Controller"));
			serviceGenerator = rebuildGenerator(serviceGenerator, StringCaseUtil.toUpper("Service"));
			repositoryGenerator = rebuildGenerator(repositoryGenerator, StringCaseUtil.toUpper("Repository"));
		}
	}
	
	@Override
	public void validate() {
		
	}

	private BeanGenerator rebuildGenerator(BeanGenerator generator, String suffix) {
		suffix = StringCaseUtil.toUpper(suffix);
		if(null == generator){
			if(StringUtils.isBlank(this.getTargetPackage())){
				ThrowableUtil.throwOf("set attribute 'targetPackage' on tag <contextGenerator /> or it's sub tags <controller />,<service />,<repository />");
			}
			generator = new BeanGenerator(this.getTargetPackage() + "." + StringCaseUtil.toLower(suffix) , suffix);
		}else{
			if(StringUtils.isBlank(generator.getSuffix())){
				generator.setSuffix(suffix);
			}else{
				generator.setSuffix(StringCaseUtil.toUpper(generator.getSuffix()));
				suffix = generator.getSuffix();
			}
			if(StringUtils.isBlank(generator.getTargetPackage())){
				if(StringUtils.isBlank(this.getTargetPackage())){
					ThrowableUtil.throwOf("set attribute 'targetPackage' on tag <contextGenerator /> or it's sub tags <controller />,<service />,<repository />");
				}
				if(suffix.equals(suffix.toUpperCase())){
					generator.setTargetPackage(this.getTargetPackage() + "." + suffix);
				}else{
					generator.setTargetPackage(this.getTargetPackage() + "." + StringCaseUtil.toLower(suffix));
				}
			}
		}
		return generator;
	}

	public BeanGenerator getControllerGenerator() {
		return controllerGenerator;
	}


	public void setControllerGenerator(BeanGenerator controllerGenerator) {
		this.controllerGenerator = controllerGenerator;
	}


	public BeanGenerator getServiceGenerator() {
		return serviceGenerator;
	}

	public void setServiceGenerator(BeanGenerator serviceGenerator) {
		this.serviceGenerator = serviceGenerator;
	}

	public BeanGenerator getRepositoryGenerator() {
		return repositoryGenerator;
	}

	public void setRepositoryGenerator(BeanGenerator repositoryGenerator) {
		this.repositoryGenerator = repositoryGenerator;
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

	public Boolean getInUsing() {
		return inUsing;
	}

	public void setInUsing(Boolean inUsing) {
		this.inUsing = inUsing;
	}


}
