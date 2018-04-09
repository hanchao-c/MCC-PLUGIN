package org.winber.MCC_plugin;

import org.apache.maven.plugin.logging.Log;
import org.mybatis.generator.internal.NullProgressCallback;

public class MavenProgressCallback extends NullProgressCallback {

    private Log log;
    private boolean verbose;

    /**
     * 
     */
    public MavenProgressCallback(Log log, boolean verbose) {
        super();
        this.log = log;
        this.verbose = verbose;
    }

    @Override
    public void startTask(String subTaskName) {
        if (verbose) {
            log.info(subTaskName);
        }
    }
}
