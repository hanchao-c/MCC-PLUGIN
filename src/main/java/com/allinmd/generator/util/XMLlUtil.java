package com.allinmd.generator.util;

import com.allinmd.generator.MavenPluginLoggerFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.maven.plugin.logging.Log;

public class XMLlUtil {

    private static final Log logger = MavenPluginLoggerFactory.getLogger();
    private static final XmlMapper xmlMapper = new XmlMapper();

    static {
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T toJavaObject(String xmlString, Class<T> clz) {

        try {
            return xmlMapper.readValue(xmlString, clz);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

}
