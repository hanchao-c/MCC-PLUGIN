package org.winber.MCC_plugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.util.ClassloaderUtility;
import org.mybatis.generator.internal.util.messages.Messages;
import org.mybatis.generator.logging.LogFactory;

@Mojo(name = "generate",defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class MyBatisGeneratorMojo extends AbstractMojo {

    @Parameter(property="project",required=true,readonly=true)
    private MavenProject project;

    @Parameter(property="mybatis.generator.outputDirectory", defaultValue="${project.build.directory}/generated-sources/mybatis-generator", required=true)
    private File outputDirectory;

    @Parameter(property="mybatis.generator.configurationFile",defaultValue="${project.basedir}/src/main/resources/generatorConfig.xml", required=true)
    private File configurationFile;

    @Parameter(property="mybatis.generator.verbose", defaultValue="true")
    private boolean verbose;

    @Parameter(property="mybatis.generator.overwrite", defaultValue="true")
    private boolean overwrite;

    @Parameter(property="mybatis.generator.sqlScript")
    private String sqlScript;

    @Parameter(property="mybatis.generator.jdbcDriver")
    private String jdbcDriver;

    @Parameter(property="mybatis.generator.jdbcURL")
    private String jdbcURL;

    @Parameter(property="mybatis.generator.jdbcUserId")
    private String jdbcUserId;

    @Parameter(property="mybatis.generator.jdbcPassword")
    private String jdbcPassword;

    @Parameter(property="mybatis.generator.tableNames")
    private String tableNames;

    @Parameter(property="mybatis.generator.contexts")
    private String contexts;

    @Parameter(property="mybatis.generator.skip", defaultValue="false")
    private boolean skip;
    

    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info( "MyBatis generator is skipped." );
            return;
        }

    	LogFactory.setLogFactory(new MavenLogFactory(this));
        List<Resource> resources = project.getResources();
        List<String> resourceDirectories = new ArrayList<String>();
        for (Resource resource: resources) {
            resourceDirectories.add(resource.getDirectory());
        }
        ClassLoader cl = ClassloaderUtility.getCustomClassloader(resourceDirectories);
        ObjectFactory.addResourceClassLoader(cl);

        if (configurationFile == null) {
            throw new MojoExecutionException(Messages.getString("RuntimeError.0"));
        }

        List<String> warnings = new ArrayList<String>();

        if (!configurationFile.exists()) {
            throw new MojoExecutionException(Messages.getString("RuntimeError.1", configurationFile.toString()));
        }

        runScriptIfNecessary();

        try {
            ConfigurationParser cp = new ConfigurationParser(project.getProperties(), warnings);
            Configuration config = cp.parseConfiguration(configurationFile);
            List<Context> contexts = config.getContexts();
            for (Context context : contexts) {
        		
        		Properties properties = context.getProperties();
        		Map<Object,Object> $params = new Hashtable<Object, Object>();
        		$params.putAll(properties);
        		getLog().info(contextProxyGet($params, context, "controller"));
        		getLog().info(contextProxyGet($params, context, "service"));
        		getLog().info(contextProxyGet($params, context, "repository"));
        		
            	context.addProperty("javaFileEncoding", "UTF-8");

            	//PLUGIN
            	PluginConfiguration pluginConfiguration1 = new PluginConfiguration();
        		pluginConfiguration1.setConfigurationType("org.mybatis.generator.plugins.SerializablePlugin");
        		context.addPluginConfiguration(pluginConfiguration1);
        		
        		PluginConfiguration pluginConfiguration2 = new PluginConfiguration();
        		pluginConfiguration2.setConfigurationType("org.mybatis.generator.plugins.EqualsHashCodePlugin");
        		context.addPluginConfiguration(pluginConfiguration2);
        		
        		PluginConfiguration pluginConfiguration3 = new PluginConfiguration();
        		pluginConfiguration3.setConfigurationType("org.mybatis.generator.plugins.CaseInsensitiveLikePlugin");
        		context.addPluginConfiguration(pluginConfiguration3);
        		
        		PluginConfiguration pluginConfiguration4 = new PluginConfiguration();
        		pluginConfiguration4.setConfigurationType("org.mybatis.generator.plugins.ToStringPlugin");
        		context.addPluginConfiguration(pluginConfiguration4);
        		
        		PluginConfiguration pluginConfiguration5 = new PluginConfiguration();
        		pluginConfiguration5.setConfigurationType("tk.mybatis.mapper.generator.MapperPlugin");
        		pluginConfiguration5.addProperty("mappers", "tk.mybatis.mapper.common.Mapper");
        		pluginConfiguration5.addProperty("javaFileEncoding", "UTF-8");
        		context.addPluginConfiguration(pluginConfiguration5);

        		PluginConfiguration pluginConfiguration6 = new PluginConfiguration();
        		pluginConfiguration6.setConfigurationType("org.mybatis.generator.plugins.FluentBuilderMethodsPlugin");
        		context.addPluginConfiguration(pluginConfiguration6);
        		
        		//FILE PATH
        		JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = context.getJavaModelGeneratorConfiguration();
        		javaModelGeneratorConfiguration.setTargetPackage($replace(javaModelGeneratorConfiguration.getTargetPackage(), $params));
        		javaModelGeneratorConfiguration.addProperty("enableSubPackages", "true");
        		javaModelGeneratorConfiguration.addProperty("trimStrings", "true");
        		
        		SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = context.getSqlMapGeneratorConfiguration();
        		sqlMapGeneratorConfiguration.setTargetPackage($replace(sqlMapGeneratorConfiguration.getTargetPackage(), $params));
        		sqlMapGeneratorConfiguration.addProperty("enableSubPackages", "true");
        		
        		JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = context.getJavaClientGeneratorConfiguration();
        		javaClientGeneratorConfiguration.setTargetPackage($replace(javaClientGeneratorConfiguration.getTargetPackage(), $params));
        		javaClientGeneratorConfiguration.addProperty("enableSubPackages", "true");
        		
        		//TABLE
        		List<TableConfiguration> tableConfigurations = context.getTableConfigurations();
        		getLog().info("tableConfigurations" + tableConfigurations);
        		for (TableConfiguration tableConfiguration : tableConfigurations) {
        			tableConfiguration.setCountByExampleStatementEnabled(false);
        			getLog().info(tableConfiguration.getDomainObjectName());
				}

			}
            
            ShellCallback callback = new MavenShellCallback(this, overwrite);
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            myBatisGenerator.generate(new MavenProgressCallback(getLog(), verbose), new HashSet<String>(), new HashSet<String>());
        } catch (XMLParserException e) {
            for (String error : e.getErrors()) {
                getLog().error(error);
            }
            throw new MojoExecutionException(e.getMessage());
        } catch (SQLException e) {
            throw new MojoExecutionException(e.getMessage());
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage());
        } catch (InvalidConfigurationException e) {
            for (String error : e.getErrors()) {
                getLog().error(error);
            }

            throw new MojoExecutionException(e.getMessage());
        } catch (InterruptedException e) {
        } finally {
		}

        for (String error : warnings) {
            getLog().warn(error);
        }

        if (project != null && outputDirectory != null && outputDirectory.exists()) {
            project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
            Resource resource = new Resource();
            resource.setDirectory(outputDirectory.getAbsolutePath());
            resource.addInclude("**/*.xml");
            project.addResource(resource);
        }
    }
    
    private String $replace(String source, Map<Object, Object> $params) {
    	if(source.contains("${")){
    		String rowKey = source.substring(source.indexOf("${"), source.indexOf("}") + 1);
			String value = get($params, rowKey);
			String replace = source.replace(rowKey, value);
			return replace;
    	}
		return source;
	}

	public String contextProxyGet(Map<Object,Object> params, Context context, String key){
    	String property = context.getProperty(key);
    	if(StringUtils.isBlank(property)){
    		return property;
    	}
    	if(property.contains("${")){
			String rowKey = property.substring(property.indexOf("${"), property.indexOf("}") + 1);
			String value = get(params, rowKey);
			String replace = property.replace(rowKey, value);
			params.put(key, replace);
			return replace;
		}
    	
		return context.getProperty(key);
    }

    
    public String get(Map<Object,Object> params, String key){
    	return (String)params.get(key.substring(key.indexOf("${") + 2, key.indexOf("}")));
    }
    

    private void runScriptIfNecessary() throws MojoExecutionException {
        if (sqlScript == null) {
            return;
        }

        SqlScriptRunner scriptRunner = new SqlScriptRunner(sqlScript, jdbcDriver, jdbcURL, jdbcUserId, jdbcPassword);
        scriptRunner.setLog(getLog());
        scriptRunner.executeScript();
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }
}
