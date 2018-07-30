package com.jlbank.plugins.generater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.plexus.util.IOUtil;

import com.google.common.io.Files;
import com.jlbank.plugins.resource.ClassPathResource;

@Mojo(name = "generate-help",defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GeneratorHelpMojo extends AbstractMojo{

	public void execute() throws MojoExecutionException, MojoFailureException {
		copy("template/generator-config-simple.xml");
		copy("template/generator-config-specific.xml");
		copy("template/context/controller.ftl");
		copy("template/context/mock.ftl");
		copy("template/context/repository.ftl");
		copy("template/context/repositoryImpl.ftl");
		copy("template/context/service.ftl");
		copy("template/context/serviceImpl.ftl");
	}
	
	public void copy(String filePath) {
		ClassPathResource classPathResource = new ClassPathResource(filePath);
		FileOutputStream outputStream = null;
		InputStream inputStream = classPathResource.getAsInputStream();
		try {
			File file = new File("src/main/resources/" + StringUtils.substringAfter(filePath, "/"));
			if(file.exists()) {
				file.delete();
			}
			Files.createParentDirs(file);
			outputStream = new FileOutputStream(file);
			IOUtil.copy(inputStream, outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtil.close(inputStream);
			IOUtil.close(outputStream);		
		}
	}
	
}
