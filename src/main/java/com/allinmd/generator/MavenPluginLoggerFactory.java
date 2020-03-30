package com.allinmd.generator;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

public class MavenPluginLoggerFactory {

    public static Log getLogger() {
        return new SystemStreamLog();
    }
}
