package com.allinmd.generator.exe;

import com.allinmd.generator.MavenPluginLoggerFactory;
import com.google.common.base.Throwables;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.IOUtil;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class JavaObjectGenerateExecutor {

    private String name;

    private String fullJavaObjectName;

    private String templateContent;

    private String targetPackage;

    private String targetProject;

    private JavaObjectFieldSetting javaObjectFieldSetting;

    private Map<String, Object> globalArgs;


    public void execute() {
        if (StringUtils.equalsAny(this.name, "model", "mapper")) {
            return;
        }
        DefaultShellCallback defaultShellCallback = new DefaultShellCallback(false);
        try {
            File directory = defaultShellCallback.getDirectory(this.targetProject, this.targetPackage);
            File targetFile = new File(directory, fullJavaObjectName + ".java");
            try {
                Configuration configuration = configuration();
                Template template = new Template(this.name, templateContent, configuration);
                String processedContent = process(template, globalArgs);
                writeFile(targetFile, processedContent, null);
            } catch (IOException e) {
                MavenPluginLoggerFactory.getLogger().error("生成文件失败");
            }
        } catch (ShellException e) {
            Throwables.propagate(e);
        }

    }

    private void writeFile(File file, String content, String fileEncoding) throws IOException {
        if (file.exists()) {
            String targetPath =
                    file.getAbsolutePath() + "." + new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss").format(new Date());
            file = new File(targetPath);
        }
        FileOutputStream fos = new FileOutputStream(file, false);
        OutputStreamWriter osw;
        if (fileEncoding == null) {
            osw = new OutputStreamWriter(fos);
        } else {
            osw = new OutputStreamWriter(fos, fileEncoding);
        }

        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(content);
        bw.close();
    }

    private String process(Template template, Map<String, ?> dataModel) {
        StringWriter writer = null;
        try {
            writer = new StringWriter();
            template.process(dataModel, writer);
            return writer.toString();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        } finally {
            IOUtil.close(writer);
        }
    }

    private Configuration configuration() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        StringTemplateLoader templateLoader = new StringTemplateLoader();
        configuration.setTemplateLoader(templateLoader);
        configuration.setDefaultEncoding("UTF-8");
        return configuration;
    }


}
