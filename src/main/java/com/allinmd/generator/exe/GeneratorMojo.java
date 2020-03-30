package com.allinmd.generator.exe;

import com.allinmd.generator.config.JavaObjectConfiguration;
import com.allinmd.generator.config.JavaObjectGeneratorConfiguration;
import com.allinmd.generator.config.MybatisGenerateConfig;
import com.allinmd.generator.resource.ClassPathResource;
import com.allinmd.generator.resource.FilePathResource;
import com.allinmd.generator.util.ValidateXML;
import com.allinmd.generator.util.XMLlUtil;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.IOUtil;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.NullProgressCallback;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GeneratorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.basedir}/src/main/resources/generate-config.xml", required = true)
    private File configurationFile;

    @Parameter(defaultValue = "${project.basedir}/", required = true)
    private String projectResourceBaseDir;

    private final Log logger = getLog();

    public void execute() throws MojoExecutionException, MojoFailureException {
        // 校验配置文件
        validateConfigFile();
        // 解析xml为java
        JavaObjectConfiguration javaObjectConfiguration = parseToConfig();
        try {
            // 原生mybatis-generate.xml配置文件
            String mybatisGenerateConfigPath = getMybatisGenerateConfig(javaObjectConfiguration);
            String mybatisGeneratorConfigContent = new FilePathResource(mybatisGenerateConfigPath).getAsString();
            // 生成mybatis相关代码
            Configuration mybatisConfiguration = generateMyBatisCode(mybatisGeneratorConfigContent);
            generateCustomCode(javaObjectConfiguration, mybatisConfiguration);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void generateCustomCode(JavaObjectConfiguration javaObjectConfiguration,
            Configuration mybatisConfiguration) {
        // 解析template内容
        Map<String, String> generateContentMap = parseToTemplateContentMap(javaObjectConfiguration);
        // 解析为TemplateSettings
        TemplateSettings templateSettings =
                parseToTemplateSettings(javaObjectConfiguration, mybatisConfiguration, generateContentMap);
        // 代码生成
        generateCode(templateSettings);
    }

    private void generateCode(TemplateSettings templateSettings) {
        List<JavaObjectGenerateExecutor> allJavaObjectGenerateExecutors =
                templateSettings.getAllJavaObjectGenerateExecutor();
        for (JavaObjectGenerateExecutor javaObjectGenerateExecutor : allJavaObjectGenerateExecutors) {
            javaObjectGenerateExecutor.execute();
        }
    }

    private TemplateSettings parseToTemplateSettings(JavaObjectConfiguration javaObjectConfiguration,
            Configuration mybatisConfiguration, Map<String, String> generateContentMap) {
        TemplateSettingsProcessor templateSettingsProcessor = new TemplateSettingsProcessor();
        templateSettingsProcessor.setJavaObjectConfiguration(javaObjectConfiguration);
        templateSettingsProcessor.setMybatisConfiguration(mybatisConfiguration);
        TemplateSettings templateSettings = templateSettingsProcessor.process();
        templateSettings.setFreemarkerTemplateContents(generateContentMap);
        return templateSettings;
    }

    private Map<String, String> parseToTemplateContentMap(JavaObjectConfiguration javaObjectConfiguration) {
        List<JavaObjectGeneratorConfiguration> javaObjectGenerators = javaObjectConfiguration.getJavaObjectGenerators();
        Map<String, String> generateContentMap = Maps.newHashMap();
        for (JavaObjectGeneratorConfiguration javaObjectGeneratorConfiguration : javaObjectGenerators) {
            String templatePath = javaObjectGeneratorConfiguration.getTemplatePath();
            String templateContent;
            if (StringUtils.isBlank(templatePath)) {
                templateContent = new ClassPathResource(
                        "default-templates/" + javaObjectGeneratorConfiguration.getName() + ".ftl").getAsString();
            } else {
                templateContent = new FilePathResource(templatePath).getAsString();
            }
            generateContentMap.put(javaObjectGeneratorConfiguration.getName(), templateContent);
        }
        return generateContentMap;
    }

    private String getMybatisGenerateConfig(JavaObjectConfiguration javaObjectConfiguration) {
        String mybatisGenerateConfigPath;
        MybatisGenerateConfig mybatisGenerateConfig = javaObjectConfiguration.getMybatisGenerateConfig();
        if (null == mybatisGenerateConfig || StringUtils.isBlank(mybatisGenerateConfig.getPath())) {
            mybatisGenerateConfigPath = configurationFile.getParent() + "/generate-config.xml";
        } else {
            mybatisGenerateConfigPath = mybatisGenerateConfig.getPath();
        }
        return mybatisGenerateConfigPath;
    }

    private void validateConfigFile() throws MojoExecutionException {
        if (null == configurationFile || !configurationFile.exists()) {
            logger.error("配置文件不存在");
            throw new MojoExecutionException("配置文件不存在");
        }
        // 获取配置文件
        StreamSource streamSource = new StreamSource(configurationFile);
        // 配置文件xsd验证
        ValidateXML.validateGeneratorXML(streamSource);
    }

    private JavaObjectConfiguration parseToConfig() throws MojoExecutionException {
        JavaObjectConfiguration javaObjectConfiguration;
        try {
            String config = Files.toString(configurationFile, Charsets.UTF_8);
            javaObjectConfiguration = XMLlUtil.toJavaObject(config, JavaObjectConfiguration.class);
        } catch (IOException e) {
            throw new MojoExecutionException("解析配置文件失败", e);
        }
        return javaObjectConfiguration;
    }

    private Configuration generateMyBatisCode(String content) throws MojoExecutionException {
        logger.info("");
        logger.info("Generate mybatis start");
        logger.info("------------------------------------------------------------------------");
        List<String> warnings = new ArrayList<String>();
        StringReader stringReader = null;
        Configuration mybatisConfig = null;
        try {
            stringReader = new StringReader(content);
            ConfigurationParser configurationParser = new ConfigurationParser(null, warnings);
            mybatisConfig = configurationParser.parseConfiguration(stringReader);
            MyBatisGenerator myBatisGenerator =
                    new MyBatisGenerator(mybatisConfig, new DefaultShellCallback(true), warnings);
            myBatisGenerator.generate(new NullProgressCallback() {
                @Override
                public void startTask(String taskName) {
                    logger.info(taskName);
                }
            }, new HashSet<String>(), new HashSet<String>(), true);
        } catch (XMLParserException e) {
            for (String error : e.getErrors()) {
                logger.error(error);
            }
            throw new MojoExecutionException(e.getMessage());
        } catch (SQLException e) {
            throw new MojoExecutionException(e.getMessage());
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage());
        } catch (InvalidConfigurationException e) {
            for (String error : e.getErrors()) {
                logger.error(error);
            }
            throw new MojoExecutionException(e.getMessage());
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } finally {
            IOUtil.close(stringReader);
        }
        for (String error : warnings) {
            logger.warn(error);
        }
        logger.info("------------------------------------------------------------------------");
        logger.info("Generate mybatis end");
        logger.info("");
        return mybatisConfig;
    }

}
