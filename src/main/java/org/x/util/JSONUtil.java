package org.x.util;

import org.apache.maven.plugin.logging.Log;
import org.x.MavenPluginLoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {

	private static final Log logger = MavenPluginLoggerFactory.getLogger();
	
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	static {
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	public static <T> T toJavaObject(String xmlString, Class<T> clz){
		
		try {
			return objectMapper.readValue(xmlString, clz);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		
	}

	public static String toJSONString(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
}
