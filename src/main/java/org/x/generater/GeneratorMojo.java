package org.x.generater;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.IOUtil;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.NullProgressCallback;
import org.x.generater.xml.BeanGenerator;
import org.x.generater.xml.ContextGenerator;
import org.x.generater.xml.Generator;
import org.x.generater.xml.JdbcConnection;
import org.x.generater.xml.MybatisGenerator;
import org.x.generater.xml.PropertiesHandler;
import org.x.generater.xml.Property;
import org.x.generater.xml.Table;
import org.x.resource.ClassPathResource;
import org.x.resource.FilePathResource;
import org.x.resource.Resource;
import org.x.util.StringCaseUtil;
import org.x.util.XMLlUtil;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

@Mojo(name = "generate",defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GeneratorMojo extends AbstractMojo {

	
    @Parameter(defaultValue = "${project.basedir}/src/main/resources/generator.xml", required = true)
    private File configurationFile;
    
    @Parameter(defaultValue = "${project.basedir}/", required = true)
    private String projectResourceBaseDir;
    
    private final Log logger = getLog();
    
    private final Map<GenerateResourceType, Resource> resources = Maps.newHashMap();
    private final Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
    private final Map<String, Object> mybatisGenerateDataModel =  Maps.newHashMap();
    private final Map<String, Map<String, ContextObjectDataModel>> contextGeneratorDataModel = Maps.newHashMap();
    private final Map<String, Object> propertiesMap = Maps.newHashMap();
    private org.mybatis.generator.config.Configuration config;
    List<ContextFile> contextFiles = Lists.newArrayList();
    void init(){
    	loadGenerateConfigs();
		validateGeneratorXML();
		fillFreemarkerTemplates();
		configuration.setEncoding(Locale.CHINA, "UTF-8");
    }
    
	public void execute() throws MojoExecutionException, MojoFailureException {
		init();
		Generator generator = parseGenerateXMLConfig();
		generator.rebuild();
		MybatisGenerator mybatisGenerator = generator.getMybatisGenerator();
		ContextGenerator contextGenerator = generator.getContextGenerator();
		List<Table> tables = generator.getTables();
		buildMybatisGenerateModel(mybatisGenerator, tables);
		generateMyBatisCode(mybatisGenerator.getOverwrite(), tables);
		if(generator.skippedContextGenerator()){
			logger.info("Context generation skipped");
			return;
		}
		replaceTemplates(contextGenerator);
		buildContextGeneratorModel(generator, tables);
		List<Context> contexts = config.getContexts();
		generateContextCode(tables, contextGenerator, contexts);
	}
	
	private void replaceTemplates(ContextGenerator contextGenerator) {
		BeanGenerator modelMockGenerator = contextGenerator.getModelMockGenerator();
		replaceTemplate(GenerateResourceType.FTL_CONTROLLER, contextGenerator.getControllerGenerator());
		replaceTemplate(GenerateResourceType.FTL_SERVICE, contextGenerator.getServiceGenerator());
		replaceTemplate(GenerateResourceType.FTL_SERVICE_IMPL, contextGenerator.getServiceImplGenerator());
		replaceTemplate(GenerateResourceType.FTL_REPOSITORY, contextGenerator.getRepositoryGenerator());
		replaceTemplate(GenerateResourceType.FTL_REPOSITORY_IMPL, contextGenerator.getRepositoryImplGenerator());
		if(null != modelMockGenerator){
			replaceTemplate(GenerateResourceType.MOCK, modelMockGenerator);
		}
	}

	private void replaceTemplate(GenerateResourceType type, BeanGenerator generator) {
		if(StringUtils.isNotBlank(generator.getTemplatePath())){
			String filePath;
			if(StringUtils.startsWithAny(generator.getTemplatePath(), "/src/main", "src/main")){
				filePath = projectResourceBaseDir + cutStartSeparater(generator.getTemplatePath());
			}else{
				filePath = projectResourceBaseDir + "src/main/resources/" + cutStartSeparater(generator.getTemplatePath());
			}
			File file = new File(filePath);
			if(!file.exists()){
				logger.error("Template not found. " + filePath);
				return;
			}
			logger.info("Replace template of '" + type.getSubject() + "' with file : " + filePath);
			StringTemplateLoader templateLoader = (StringTemplateLoader) configuration.getTemplateLoader();
			templateLoader.putTemplate(type.name(), new FilePathResource(filePath).getAsString());
		}
	}
	
	private String cutStartSeparater(String source){
		if(StringUtils.startsWith(source, "/")){
			return StringUtils.substring(source, 1);
		}
		return source;
	}
	
	private void generateContextCode(List<Table> tables, ContextGenerator contextGenerator, List<Context> contexts) {
		logger.info("");
		logger.info("Generate context start");
		logger.info("------------------------------------------------------------------------");
		Set<Entry<String,Map<String,ContextObjectDataModel>>> entrySet = contextGeneratorDataModel.entrySet();
		List<String> warnings = Lists.newArrayList();
		List<String> savings = Lists.newArrayList();
		for (Entry<String, Map<String, ContextObjectDataModel>> entry : entrySet) {
			Map<String, ContextObjectDataModel> model = entry.getValue();
			contextFiles.add(processFile(GenerateResourceType.FTL_CONTROLLER, model, contextGenerator.getControllerGenerator().getOverwrite(), null));
			contextFiles.add(processFile(GenerateResourceType.FTL_SERVICE, model, contextGenerator.getServiceGenerator().getOverwrite(), null));
			contextFiles.add(processFile(GenerateResourceType.FTL_SERVICE_IMPL, model, contextGenerator.getServiceImplGenerator().getOverwrite(), null));
			contextFiles.add(processFile(GenerateResourceType.FTL_REPOSITORY, model, contextGenerator.getRepositoryGenerator().getOverwrite(), null));
			contextFiles.add(processFile(GenerateResourceType.FTL_REPOSITORY_IMPL, model, contextGenerator.getRepositoryImplGenerator().getOverwrite(), null));
			if(null != contextGenerator.getModelMockGenerator()) {
				Map<String, Object> extra = Maps.newHashMap();
				for (Context context : contexts) {
					List<IntrospectedTable> introspectedTables = getIntrospectedTables(context);
					for (IntrospectedTable introspectedTable : introspectedTables) {
						String domainObjectName = introspectedTable.getTableConfiguration().getDomainObjectName();
						ContextObjectDataModel contextObjectDataModel = model.get(GenerateResourceType.MODEL.getSubject());
						if(Objects.equal(domainObjectName, contextObjectDataModel.getObjectName())) {
							List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
							for (IntrospectedColumn introspectedColumn : allColumns) {
								introspectedColumn.setJavaProperty(StringCaseUtil.toUpper(introspectedColumn.getJavaProperty()));
								introspectedColumn.setRemarks(introspectedColumn.getRemarks());
							}
							extra.put("introspectedColumns", introspectedTable.getAllColumns());
							break;
						}
					}
				}
				contextFiles.add(processFile(GenerateResourceType.MOCK, model, contextGenerator.getModelMockGenerator().getOverwrite(), extra));
				extra.clear();
			}
			
		}
		
		String index = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
		for (ContextFile contextFile : contextFiles) {
			File file = new File(contextFile.getFilePath());
			try {
				Files.createParentDirs(file);
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			if(!file.exists()){
				write(contextFile, file, savings);
			}else{
				if(contextFile.isOverwirte()){
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
		for (String saving : savings) {
			logger.info(saving);
		}
		for (String warn : warnings) {
			logger.warn(warn);
		}
		logger.info("------------------------------------------------------------------------");
		logger.info("Generate context end");
		logger.info("");
	}
	
	private void write(ContextFile contextFile, File file, List<String> savings) {
		savings.add("Saving file  " + file.getName());
		BufferedWriter fileWriter = null;
		try {
			fileWriter = new BufferedWriter (new OutputStreamWriter (new FileOutputStream (file),"UTF-8"));
			fileWriter.write(contextFile.getContext());
			fileWriter.flush();
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			IOUtil.close(fileWriter);
		}
	}
	
	private ContextFile processFile(GenerateResourceType type, Map<String, ContextObjectDataModel> model, boolean overwrite, Map<String, Object> extra) {
		ContextFile contextFile = new ContextFile();
		ContextObjectDataModel contextObjectDataModel = model.get(type.getSubject());
		contextFile.setFilePath(contextObjectDataModel.getFileTargetPath());
		Map<String, Object> data = Maps.newHashMap();
		Set<Entry<String, ContextObjectDataModel>> entrySet = model.entrySet();
		for (Entry<String, ContextObjectDataModel> entry : entrySet) {
			data.put(entry.getKey(), entry.getValue());
		}
		data.putAll(this.propertiesMap);
		if(null != extra) {
			data.putAll(extra);
		}
		contextFile.setContext(process(type, data));
		contextFile.setOverwirte(overwrite);
		contextFile.setType(type);
		logger.info("Generating " + Files.getNameWithoutExtension(contextFile.getFilePath()) + " class ");
		return contextFile;
	}

	private void buildContextGeneratorModel(Generator generator, List<Table> tables) {
		ContextGenerator contextGenerator = generator.getContextGenerator();
		MybatisGenerator mybatisGenerator = generator.getMybatisGenerator();
		for (Table table : tables) {
			Map<String, ContextObjectDataModel> models = Maps.newHashMap();
			String domainObjectName = table.getDomainObjectName();
			models.put(GenerateResourceType.FTL_CONTROLLER.getSubject(), new ContextObjectDataModel(domainObjectName, contextGenerator.getControllerGenerator()));
			models.put(GenerateResourceType.FTL_SERVICE.getSubject(), new ContextObjectDataModel(domainObjectName, contextGenerator.getServiceGenerator()));
			models.put(GenerateResourceType.FTL_SERVICE_IMPL.getSubject(), new ContextObjectDataModel(domainObjectName, contextGenerator.getServiceImplGenerator()));
			models.put(GenerateResourceType.FTL_REPOSITORY.getSubject(), new ContextObjectDataModel(domainObjectName, contextGenerator.getRepositoryGenerator()));
			models.put(GenerateResourceType.FTL_REPOSITORY_IMPL.getSubject(), new ContextObjectDataModel(domainObjectName, contextGenerator.getRepositoryImplGenerator()));
			models.put(GenerateResourceType.MODEL.getSubject(), new ContextObjectDataModel(domainObjectName, mybatisGenerator.getModelGenerator()));
			models.put(GenerateResourceType.MAPPER.getSubject(), new ContextObjectDataModel(domainObjectName, mybatisGenerator.getMapperGenerator()));
			if(null != contextGenerator.getModelMockGenerator()) {
				models.put(GenerateResourceType.MOCK.getSubject(), new ContextObjectDataModel(domainObjectName, contextGenerator.getModelMockGenerator()));
			}
			contextGeneratorDataModel.put(domainObjectName, models);
		}
		
		List<Property> properties = generator.getProperties();
		for (Property property : properties) {
			propertiesMap.put("p_" + property.getName(), property.getValue());
			propertiesMap.put(property.getName(), property.getValue());
		}
	}

	private void generateMyBatisCode(boolean overwirte, List<Table> tables) throws MojoExecutionException {
		logger.info("");
		logger.info("Generate mybatis start");
		logger.info("------------------------------------------------------------------------");
        List<String> warnings = new ArrayList<String>();
        String process = process(GenerateResourceType.XML_MYBATIS_GENERATOR, mybatisGenerateDataModel);
        StringReader stringReader = null;
        try {
        	stringReader = new StringReader(process);
            ConfigurationParser configurationParser = new ConfigurationParser(null, warnings);
            config = configurationParser.parseConfiguration(stringReader);
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, new DefaultShellCallback(overwirte), warnings);
            myBatisGenerator.generate(new NullProgressCallback(){
            	@Override
            	public void startTask(String taskName) {
            		logger.info(taskName);
            	}
            }, new HashSet<String>(), new HashSet<String>());
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
        }finally {
			IOUtil.close(stringReader);
		}
        for (String error : warnings) {
            logger.warn(error);
        }
        logger.info("------------------------------------------------------------------------");
        logger.info("Generate mybatis end");
        logger.info("");
	}

	@SuppressWarnings("unchecked")
	private List<IntrospectedTable> getIntrospectedTables(Context context) {
		try {
			Field declaredField = context.getClass().getDeclaredField("introspectedTables");
			declaredField.setAccessible(true);
			return (List<IntrospectedTable>) declaredField.get(context);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage());
		} catch (NoSuchFieldException e) {
			logger.error(e.getMessage());
		} catch (SecurityException e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	
	private void buildMybatisGenerateModel(MybatisGenerator mybatisGenerator, List<Table> tables) {
		mybatisGenerateDataModel.put("mapperPackage", mybatisGenerator.getTargetPackage());
		mybatisGenerateDataModel.put("mappingPackage", mybatisGenerator.getTargetPackage());
		mybatisGenerateDataModel.put("modelPackage", mybatisGenerator.getTargetPackage());
		JdbcConnection jdbcConnection = mybatisGenerator.getJdbcConnection();
		mybatisGenerateDataModel.put("connectionURL", jdbcConnection.getUrl());
		mybatisGenerateDataModel.put("password", jdbcConnection.getPassword());
		mybatisGenerateDataModel.put("userId", jdbcConnection.getUserName());
		mybatisGenerateDataModel.put("mapperPackage", mybatisGenerator.getMapperGenerator().getTargetPackage());
		mybatisGenerateDataModel.put("mappingPackage", mybatisGenerator.getMappingGenerator().getTargetPackage());
		mybatisGenerateDataModel.put("modelPackage", mybatisGenerator.getModelGenerator().getTargetPackage());
		mybatisGenerateDataModel.put("tables", tables);
		String jdbcUrl = jdbcConnection.getUrl();
		mybatisGenerateDataModel.put("schema", jdbcUrl.substring(jdbcUrl.lastIndexOf("/") + 1, jdbcUrl.length()));
	}
	
	private Generator parseGenerateXMLConfig() {
		PropertiesHandler handler = XMLlUtil.toJavaObject(resources.get(GenerateResourceType.XML_GENERATOR).getAsString(), PropertiesHandler.class);
		String resultString = process(GenerateResourceType.XML_GENERATOR, handler.autoFill());
		return XMLlUtil.toJavaObject(resultString, Generator.class);
		
	}
	
	private String process(GenerateResourceType type, Map<String, ?> dataModel){
		StringWriter writer = null;
		try {
			Template template = configuration.getTemplate(type.name());
			template.process(dataModel, writer =  new StringWriter());
			return writer.toString();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw Throwables.propagate(e);
		} finally {
			IOUtil.close(writer);
		}
	}

	private void validateGeneratorXML() {
		logger.info("Validate generator.xml");
		try {
			SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
			URL resource = resources.get(GenerateResourceType.XSD_GENERATOR).getAsURL();
			Schema schema = schemaFactory.newSchema(resource);
			Validator validator = schema.newValidator();
			Source source = new StreamSource(resources.get(GenerateResourceType.XML_GENERATOR).getAsInputStream());
			validator.validate(source);
		} catch (Exception e) {
			logger.error("Validation fail : " + e.getMessage());
			throw Throwables.propagate(e);
		}
		logger.info("Validation  success");
	}

	private void loadGenerateConfigs() {
		resources.put(GenerateResourceType.FTL_CONTROLLER, new ClassPathResource("template/context/controller.ftl"));
		resources.put(GenerateResourceType.FTL_SERVICE, new ClassPathResource("template/context/service.ftl"));
		resources.put(GenerateResourceType.FTL_SERVICE_IMPL, new ClassPathResource("template/context/serviceImpl.ftl"));
		resources.put(GenerateResourceType.FTL_REPOSITORY, new ClassPathResource("template/context/repository.ftl"));
		resources.put(GenerateResourceType.FTL_REPOSITORY_IMPL, new ClassPathResource("template/context/repositoryImpl.ftl"));
		resources.put(GenerateResourceType.XML_MYBATIS_GENERATOR, new ClassPathResource("template/mybatis/generatorConfig.ftl"));
		resources.put(GenerateResourceType.MOCK, new ClassPathResource("template/context/mock.ftl"));
		resources.put(GenerateResourceType.XSD_GENERATOR, new ClassPathResource("template/mybatis/generator.xsd"));
		resources.put(GenerateResourceType.XML_GENERATOR, new FilePathResource(configurationFile));
	}

	private void fillFreemarkerTemplates() {
		StringTemplateLoader stringLoader = new StringTemplateLoader();
		Set<Entry<GenerateResourceType,Resource>> entrySet = resources.entrySet();
		for (Entry<GenerateResourceType, Resource> entry : entrySet) {
			stringLoader.putTemplate(entry.getKey().name(), entry.getValue().getAsString());
		}
		configuration.setTemplateLoader(stringLoader);
	}
	
}
