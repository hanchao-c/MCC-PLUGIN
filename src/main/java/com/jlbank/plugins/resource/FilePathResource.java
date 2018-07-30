package com.jlbank.plugins.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.maven.plugin.logging.Log;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Files;
import com.jlbank.plugins.MavenPluginLoggerFactory;

public class FilePathResource implements Resource{

	private final Log logger = MavenPluginLoggerFactory.getLogger();
	
	private File file;
	
	public FilePathResource(String filePath) {
		this.file = new File(filePath);
	}
	
	public FilePathResource(File file) {
		this.file = file;
	}
	
	
	@Override
	public String getAsString() {
		try {
			return Files.toString(this.getFile(), Charsets.UTF_8);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw Throwables.propagate(e);
		}
	}

	@Override
	public InputStream getAsInputStream() {
		try {
			return new FileInputStream(this.getFile());
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw Throwables.propagate(e);
		}
	}

	@Override
	public URL getAsURL() {
		return null;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	
}
