package org.x.generate.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.util.ClassloaderUtility;
import org.mybatis.generator.logging.LogFactory;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

import freemarker.cache.StringTemplateLoader;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateNotFoundException;

@Mojo(name = "generate",defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class MyBatisGeneratorMojo extends AbstractMojo {

    @Parameter(property="project",required=true,readonly=true)
    private MavenProject project;

    @Parameter(property="mybatis.generator.outputDirectory", defaultValue="${project.build.directory}/generated-sources/mybatis-generator", required=true)
    private File outputDirectory;

    @Parameter(property="mybatis.generator.configurationFile",defaultValue="${project.basedir}/src/main/resources/generator.xml", required=true)
    private File configurationFile;

	private final XmlMapper xmlMapper = new XmlMapper();
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	{
		xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public void execute() throws MojoExecutionException {
    	String xmlPath = configurationFile.getAbsolutePath();
    	logInfo("");
    	logInfo("Generator start...");
    	validateXML(xmlPath);
    	logInfo("Validation success");
    	logInfo("");
    	logInfo("Generator rebuild");
    	Generator generator = parseXmlToGennerator(xmlPath);
    	try {
			generator.rebuild();
		} catch (Exception e) {
			logInfo(e.getMessage());
			Throwables.propagate(e);
		}
    	Map<String, String> data = parseDataFromMybatisGenerator(generator);
    	
		generateMyBatisCode(generator.getMybatisGenerator(), generator.getTables());
		if(!generator.skippedContextGenerator()){
			generateContextCode(generator.getContextGenerator(), generator.getTables(), data);
		}else{
			logInfo("ContextGenerate skipped");
		}
	        
    }

	private Map<String, String> parseDataFromMybatisGenerator(Generator generator) {
		MybatisGenerator mybatisGenerator = generator.getMybatisGenerator();
    	Map<String, String> data = Maps.newHashMap();
		data.put("mapperPackage", mybatisGenerator.getMapperGenerator().getTargetPackage());
		data.put("mappingPackage", mybatisGenerator.getMappingGenerator().getTargetPackage());
		data.put("modelPackage", mybatisGenerator.getModelGenerator().getTargetPackage());
		return data;
	}
	

	private void generateContextCode(ContextGenerator contextGenerator, List<Table> tables, Map<String, String> data) {
		logInfo("");
    	logInfo("Generate context code start");
    	logInfo("------------------------------------------------------------------------");
		List<String> warnings = Lists.newArrayList();
		List<String> savings = Lists.newArrayList();
		for (Table table : tables) {
			BeanGenerator controller = contextGenerator.getControllerGenerator();
			BeanGenerator service = contextGenerator.getServiceGenerator();
			BeanGenerator repository = contextGenerator.getRepositoryGenerator();
			Map<String, String> map = Maps.newHashMap();
			String javaObjectName = toUpper(table.getDomainObjectName());
			map.put("UPPER_JAVA_NAME", toUpper(javaObjectName));
			map.put("LOWER_JAVA_NAME", toLower(javaObjectName));
			map.put("CONTROLLER_PACKAGE", controller.getTargetPackage());
			map.put("SERVICE_PACKAGE", service.getTargetPackage());
			map.put("REPOSITORY_PACKAGE", repository.getTargetPackage());
			map.put("SERVICE_IMPL_PACKAGE", service.getTargetPackage() + ".impl");
			map.put("REPOSITORY_IMPL_PACKAGE", repository.getTargetPackage() + ".impl");
			map.put("CONTROLLER_NAME", javaObjectName + toUpper(controller.getSuffix()));
			map.put("SERVICE_NAME", javaObjectName + toUpper(service.getSuffix()));
			map.put("REPOSITORY_NAME", javaObjectName + toUpper(repository.getSuffix()));
			map.put("CONTROLLER_LOW_NAME", toLower(map.get("CONTROLLER_NAME")));
			map.put("SERVICE_LOW_NAME", toLower(map.get("SERVICE_NAME")));
			map.put("REPOSITORY_LOW_NAME", toLower(map.get("REPOSITORY_NAME")));
			map.put("MODEL_PACKAGE", data.get("modelPackage"));
			map.put("MAPPER_PACKAGE", data.get("mapperPackage"));
			map.put("MAPPING_PACKAGE", data.get("mappingPackage"));
			map.put("MODEL_NAME", map.get("UPPER_JAVA_NAME"));
			map.put("MODEL_EXAMPLE_NAME", map.get("MODEL_NAME") + "Example");
			map.put("MAPPER_NAME", map.get("MODEL_NAME") + "Mapper");
			map.put("MAPPING_NAME", map.get("MODEL_NAME") + "Mapping");
			map.put("MODEL_LOW_NAME", toLower(map.get("MODEL_NAME")));
			map.put("MAPPER_LOW_NAME", toLower(map.get("MAPPER_NAME")));
			map.put("MAPPING_LOW_NAME", toLower(map.get("MAPPING_NAME")));
			
			List<ContextFile> contextFiles = Lists.newArrayList(
				buildContextFile(map, "controller.ftl", controller.buildJavaFilePath(map.get("CONTROLLER_NAME")), table.getTableName()),
				buildContextFile(map, "service.ftl", service.buildJavaFilePath(map.get("SERVICE_NAME")), table.getTableName()),
				buildContextFile(map, "serviceImpl.ftl", service.buildJavaFilePath("impl", map.get("SERVICE_NAME")  + "Impl"), table.getTableName()),
				buildContextFile(map, "repository.ftl", repository.buildJavaFilePath(map.get("REPOSITORY_NAME")), table.getTableName()),
				buildContextFile(map, "repositoryImpl.ftl", repository.buildJavaFilePath("impl", map.get("REPOSITORY_NAME") + "Impl"), table.getTableName())
			);

			boolean overwrite = contextGenerator.getOverwrite();
			String index = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
			for (ContextFile contextFile : contextFiles) {
				File file = new File(contextFile.getFilePath());
				try {
					Files.createParentDirs(file);
				} catch (IOException e) {
					logError(e.getMessage());
				}
				if(!file.exists()){
					write(contextFile, file, savings);
				}else{
					if(overwrite){
						file.delete();
						File fileOther = new File(contextFile.getFilePath());
						write(contextFile, fileOther, savings);
						warnings.add("Existing file " + fileOther.getAbsolutePath() + " was overwritten");
					}else{
						File fileOther = new File(contextFile.getFilePath() + "." + index);
						warnings.add("Existing file not overwritten, the generated file is saved as " + fileOther.getAbsolutePath());
						write(contextFile, fileOther, savings);
					}
				}
			}
		}
		for (String saving : savings) {
			logInfo(saving);
		}
		for (String warn : warnings) {
			logWarn(warn);
		}
		logInfo("------------------------------------------------------------------------");
		logInfo("Generate context code end");
		logInfo("");
	}

	private void write(ContextFile contextFile, File file, List<String> savings) {
		savings.add("Saving file  " + file.getName());
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(file);
			fileWriter.write(contextFile.getContext());
		} catch (IOException e) {
			logError(e.getMessage());
		} finally {
			IOUtil.close(fileWriter);
		}
	}
	
	
	private ContextFile buildContextFile(Map<String, String> data, String ftlName, String filePath, String tableName) {
		String nameWithoutExtension = Files.getNameWithoutExtension(filePath);
		logInfo("Generating " + nameWithoutExtension + " class for table " + tableName);
		Map<String, String> map = Maps.newHashMap(data);
		URL resource = getResource(ftlName);
		InputStream is = null;
		StringWriter writer = null;
		try {
			is = (InputStream) resource.getContent();
			String content = copyToString(is);
			Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
			StringTemplateLoader stringLoader = new StringTemplateLoader();
			stringLoader.putTemplate(ftlName, content);
			configuration.setTemplateLoader(stringLoader);
			Template template = configuration.getTemplate(ftlName, "UTF-8");
			template.process(map, writer = new StringWriter());
		} catch (Exception e) {
			throw Throwables.propagate(e);
		} finally {
			IOUtil.close(writer);
			IOUtil.close(is);
		}
		ContextFile contextFile = new ContextFile();
		contextFile.setContext(writer.toString());
		contextFile.setFilePath(filePath);
		return contextFile;
	}
	
	public static String toUpper(String str){
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	public static String toLower(String str){
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}


	private void generateMyBatisCode(MybatisGenerator generator, List<Table> tables) throws MojoExecutionException {
		logInfo("");
    	logInfo("Generate mybatis code start");
    	logInfo("------------------------------------------------------------------------");
		String mybatisGeneratorGonfig = parseToMybatisGeneratorConfig(generator, tables);
    	LogFactory.setLogFactory(new MavenLogFactory(this));
        List<Resource> resources = project.getResources();
        List<String> resourceDirectories = new ArrayList<String>();
        for (Resource resource: resources) {
            resourceDirectories.add(resource.getDirectory());
        }
        ClassLoader cl = ClassloaderUtility.getCustomClassloader(resourceDirectories);
        ObjectFactory.addResourceClassLoader(cl);
        List<String> warnings = new ArrayList<String>();
        StringReader stringReader = null;
        try {
            ConfigurationParser cp = new ConfigurationParser( project.getProperties(), warnings);
            stringReader = new StringReader(mybatisGeneratorGonfig);
            org.mybatis.generator.config.Configuration config = cp.parseConfiguration(stringReader);
            ShellCallback callback = new MavenShellCallback(this, generator.getOverwrite());
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,  callback, warnings);
            myBatisGenerator.generate(new MavenProgressCallback(getLog(), true), new HashSet<String>(), new HashSet<String>());

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
        }
        finally {
			IOUtil.close(stringReader);
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
        logInfo("------------------------------------------------------------------------");
		logInfo("Generate mybatis code end");
		logInfo("");
	}

	private String parseToMybatisGeneratorConfig(MybatisGenerator generator, List<Table> tables){
		URL resource = getResource("generatorConfig.ftl");
		InputStream is = null;
		StringWriter writer = null;
		try {
			is = (InputStream) resource.getContent();
			writer = new StringWriter();
			String content = copyToString(is);
			Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
			StringTemplateLoader stringLoader = new StringTemplateLoader();
			stringLoader.putTemplate("MYBATIS_GENERATOR_CONFIG", content);
			configuration.setTemplateLoader(stringLoader);
			Template template = configuration.getTemplate("MYBATIS_GENERATOR_CONFIG", "UTF-8");
			Map<String, Object> data = getDataFromGenerator(generator, tables);
			template.process(data, writer);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		} finally {
			IOUtil.close(writer);
			IOUtil.close(is);
		}
		return writer.toString();

	}

	private Map<String, Object> getDataFromGenerator(MybatisGenerator generator, List<Table> tables) {
		Map<String, Object> data = Maps.newHashMap();
		JdbcConnection jdbcConnection = generator.getJdbcConnection();
		data.put("connectionURL", jdbcConnection.getUrl());
		data.put("password", jdbcConnection.getPassword());
		data.put("userId", jdbcConnection.getUserName());
		data.put("mapperPackage", generator.getMapperGenerator().getTargetPackage());
		data.put("mappingPackage", generator.getMappingGenerator().getTargetPackage());
		data.put("modelPackage", generator.getModelGenerator().getTargetPackage());
		data.put("tables", tables);
		String jdbcUrl = jdbcConnection.getUrl();
		data.put("schema", jdbcUrl.substring(jdbcUrl.lastIndexOf("/") + 1, jdbcUrl.length()));
		return data;
	}
    
    public Generator parseXmlToGennerator(String xmlPath){
		File file = new File(xmlPath);
		PropertiesHandler properties = parseProperties(file);
		StringWriter stringWriter = null;
		try {
			stringWriter = new StringWriter();
			stringWriter = (StringWriter) processToTemplate(file, properties.autoFill(), stringWriter);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}finally {
			IOUtil.close(stringWriter);
		}
		try {
			return xmlMapper.readValue(stringWriter.toString(), Generator.class);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	private Writer processToTemplate(File file, Object data, Writer writer) throws IOException,
			TemplateNotFoundException, MalformedTemplateNameException, ParseException, TemplateException {
		Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
		configuration.setDirectoryForTemplateLoading(new File(file.getParent()));
		configuration.setDefaultEncoding("UTF-8");
		configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		configuration.setLogTemplateExceptions(false);
		Template template = configuration.getTemplate(file.getName());
		template.process(data, writer);
		return writer;
	}

	private PropertiesHandler parseProperties(File file) {
		PropertiesHandler properties;
		try {
			properties = xmlMapper.readValue(file, PropertiesHandler.class);
		} catch (IOException e) {
			logInfo(e.getMessage());
			throw Throwables.propagate(e);
		}
		return properties;
	}
    
    public void logInfo(Object info){
    	if(null != info){
    		getLog().info(info.toString());
    	}
    }
    
    public void logWarn(Object info){
    	if(null != info){
    		getLog().warn(info.toString());
    	}
    }
    
    public void logError(Object error){
    	if(null != error){
    		getLog().error(error.toString());
    	}
    }
	public String copyToString(InputStream in) throws IOException {
		StringBuilder out = new StringBuilder();
		InputStreamReader reader = new InputStreamReader(in, Charsets.UTF_8);
		char[] buffer = new char[1024];
		int bytesRead = -1;
		while ((bytesRead = reader.read(buffer)) != -1) {
			out.append(buffer, 0, bytesRead);
		}
		return out.toString();
	}
	
    public boolean validateXML(String xmlPath) {
    	logInfo("GenerateConfig path : " + xmlPath);
    	logInfo("Do validate xmlConfig by generator.xsd");
		SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		Schema schema;
		try {
			URL resource = getResource("generator.xsd");
			schema = schemaFactory.newSchema(resource);
		} catch (SAXException e) {
			logError("Validation fail : " + e.getMessage());
			throw Throwables.propagate(e);
		} catch (Exception e) {
			logError("Validation fail : " + e.getMessage());
			throw Throwables.propagate(e);
		}
		Validator validator = schema.newValidator();
		Source source = new StreamSource(xmlPath);
		try {
			validator.validate(source);
		} catch (Exception e) {
			logError("Validation fail : " + e.getMessage());
			throw Throwables.propagate(e);
		}
		return true;
	}
    
    private URL getResource(String fileName){
    	return this.getClass().getClassLoader().getResource(fileName);
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }
}
