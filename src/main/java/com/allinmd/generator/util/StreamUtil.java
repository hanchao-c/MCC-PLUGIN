package com.allinmd.generator.util;

import com.allinmd.generator.MavenPluginLoggerFactory;
import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamUtil {

    private final static Log logger = MavenPluginLoggerFactory.getLogger();

    public static String toString(InputStream in) {
        StringBuilder out = new StringBuilder();
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(in, Charsets.UTF_8);
            char[] buffer = new char[1024];
            int bytesRead = -1;
            while ((bytesRead = reader.read(buffer)) != -1) {
                out.append(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw Throwables.propagate(e);
        } finally {
            IOUtil.close(reader);
        }
        return out.toString();
    }
}
