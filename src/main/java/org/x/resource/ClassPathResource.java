package org.x.resource;

import java.io.InputStream;
import java.net.URL;

import org.x.util.StreamUtil;

public class ClassPathResource implements Resource {

	private String index;
	private ClassLoader classLoader;

	public ClassPathResource(String index) {
		this(index, null);
	}

	public ClassPathResource(String index, ClassLoader classLoader) {
		super();
		this.index = index;
		if(null == classLoader){
			this.classLoader = this.getClass().getClassLoader();
		}else{
			this.classLoader = classLoader;
		}
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public String getAsString() {
		return StreamUtil.toString(getAsInputStream());
	}

	@Override
	public InputStream getAsInputStream() {
		return this.classLoader.getResourceAsStream(this.index);
	}

	@Override
	public URL getAsURL() {
		return this.classLoader.getResource(this.index);
	}

}
