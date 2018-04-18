package org.x.generater.xml;

import org.apache.commons.lang3.StringUtils;
import org.x.util.StringCaseUtil;
import org.x.util.ThrowableUtil;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Objects;

public class ContextGenerator implements Rebuilder{

	@JacksonXmlProperty(localName = "controller")
	private BeanGenerator controllerGenerator;
	@JacksonXmlProperty(localName = "service")
	private BeanGenerator serviceGenerator;
	@JacksonXmlProperty(localName = "serviceImpl")
	private BeanGenerator serviceImplGenerator;
	@JacksonXmlProperty(localName = "repository")
	private BeanGenerator repositoryGenerator;
	@JacksonXmlProperty(localName = "repositoryImpl")
	private BeanGenerator repositoryImplGenerator;

	@JacksonXmlProperty(isAttribute = true)
	private String targetPackage;
	@JacksonXmlProperty(isAttribute = true)
	private Boolean overwrite = Boolean.TRUE;
	@JacksonXmlProperty(isAttribute = true)
	private Boolean inUsing = Boolean.TRUE;
	
	public enum ImplType{
		SERVICE,
		REPOSITORY;
	}
	
	@Override
	public void rebuild() {
		if(inUsing){
			controllerGenerator = rebuildGenerator(controllerGenerator, StringCaseUtil.toUpper("Controller"), false, null);
			serviceGenerator = rebuildGenerator(serviceGenerator, StringCaseUtil.toUpper("Service"), false, null);
			serviceImplGenerator = rebuildGenerator(serviceImplGenerator, StringCaseUtil.toUpper("ServiceImpl"), true, ImplType.SERVICE);
			repositoryGenerator = rebuildGenerator(repositoryGenerator, StringCaseUtil.toUpper("Repository"), false, null);
			repositoryImplGenerator = rebuildGenerator(repositoryImplGenerator, StringCaseUtil.toUpper("RepositoryImpl"), true, ImplType.REPOSITORY);
		}
	}
	
	@Override
	public void validate() {
		
	}

	private BeanGenerator rebuildGenerator(BeanGenerator generator, String suffix, boolean impl, ImplType type) {
		suffix = StringCaseUtil.toUpper(suffix);
		if(null == generator){
			if(StringUtils.isBlank(this.getTargetPackage())){
				ThrowableUtil.throwOf("set attribute 'targetPackage' on tag <contextGenerator /> or it's sub tags <controller />,<service />,<repository />");
			}
			if(Objects.equal(type, ImplType.SERVICE)){
				generator = new BeanGenerator(serviceGenerator.getTargetPackage() + ".impl" , serviceGenerator.getSuffix() + "Impl");
			}else if(Objects.equal(type, ImplType.REPOSITORY)){
				generator = new BeanGenerator(repositoryGenerator.getTargetPackage() + ".impl" , repositoryGenerator.getSuffix() + "Impl");
			}else{
				generator = new BeanGenerator(this.getTargetPackage() + "." + StringCaseUtil.toLower(suffix) , suffix);
			}
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
				if(Objects.equal(type, ImplType.SERVICE)){
					if(StringUtils.isBlank(generator.getTargetPackage())){
						generator.setTargetPackage(serviceGenerator.getTargetPackage() + ".impl");
					}
					if(StringUtils.isBlank(generator.getSuffix())){
						generator.setSuffix(serviceGenerator.getSuffix() + "Impl");
					}
				}else if(Objects.equal(type, ImplType.REPOSITORY)){
					if(StringUtils.isBlank(generator.getTargetPackage())){
						generator.setTargetPackage(repositoryGenerator.getTargetPackage() + ".impl");
					}
					if(StringUtils.isBlank(generator.getSuffix())){
						generator.setSuffix(repositoryGenerator.getSuffix() + "Impl"	);
					}
				}else{
					if(suffix.equals(suffix.toUpperCase())){
						generator.setTargetPackage(this.getTargetPackage() + "." + suffix);
					}else{
						generator.setTargetPackage(this.getTargetPackage() + "." + StringCaseUtil.toLower(suffix));
					}
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

	public BeanGenerator getServiceImplGenerator() {
		return serviceImplGenerator;
	}

	public void setServiceImplGenerator(BeanGenerator serviceImplGenerator) {
		this.serviceImplGenerator = serviceImplGenerator;
	}

	public BeanGenerator getRepositoryGenerator() {
		return repositoryGenerator;
	}

	public void setRepositoryGenerator(BeanGenerator repositoryGenerator) {
		this.repositoryGenerator = repositoryGenerator;
	}

	public BeanGenerator getRepositoryImplGenerator() {
		return repositoryImplGenerator;
	}

	public void setRepositoryImplGenerator(BeanGenerator repositoryImplGenerator) {
		this.repositoryImplGenerator = repositoryImplGenerator;
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
