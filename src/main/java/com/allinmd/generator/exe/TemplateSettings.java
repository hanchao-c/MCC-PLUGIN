package com.allinmd.generator.exe;

import com.allinmd.generator.config.JavaObjectGeneratorConfiguration;
import com.allinmd.generator.config.Property;
import com.allinmd.generator.util.StringCaseUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Getter
@Setter
@ToString
public class TemplateSettings {

    private Map<String, String> templateTempArgs = Maps.newHashMap();
    private List<JavaObjectFieldSetting> javaObjectFieldSettings;
    List<JavaObjectGeneratorConfiguration> javaObjectGeneratorConfigurations;
    private List<Property> properties;
    private Map<String, SortedMap<String, Object>> globalTemplateArgsContainer = Maps.newHashMap();
    private Context context;
    private Map<String, String> freemarkerTemplateContents;

    public TemplateSettings resolve() {
        List<Property> globalProperties = this.properties;
        if (null == this.properties) {
            globalProperties = Lists.newArrayList();
        }
        if (globalProperties.size() > 0) {
            for (Property property : globalProperties) {
                property.valid();
                this.templateTempArgs.put("p_" + property.getName(), property.getValue());
            }
        }
        if (null == this.javaObjectGeneratorConfigurations) {
            this.javaObjectGeneratorConfigurations = Lists.newArrayList();
        }

        for (JavaObjectFieldSetting javaObjectFieldSetting : javaObjectFieldSettings) {
            SortedMap<String, Object> globalTemplateArgs = new TreeMap<String, Object>(this.templateTempArgs);
            String domainObjectName = javaObjectFieldSetting.getDomainObjectName();
            Map<String, String> javaObjectSetting = Maps.newHashMap();
            javaObjectGeneratorConfigurations.add(parseToModelGenerator(context));
            javaObjectGeneratorConfigurations.add(parseToMapperGenerator(context));
            for (JavaObjectGeneratorConfiguration javaObjectGenerator : javaObjectGeneratorConfigurations) {
                String name = javaObjectGenerator.getName();
                if ("model".equals(name)) {
                    javaObjectSetting.put(name + "_" + "name", domainObjectName);
                    javaObjectSetting.put(name + "_" + "name_lower", StringCaseUtil.toLower(domainObjectName));
                } else {
                    String prefix = StringUtils.trimToEmpty(javaObjectGenerator.getPrefix());
                    javaObjectSetting.put(name + "_" + "name", prefix + domainObjectName +
                            StringCaseUtil
                                    .toUpper(javaObjectGenerator.getName()));
                    javaObjectSetting.put(name + "_" + "name_lower", StringCaseUtil.toLower(domainObjectName) +
                            StringCaseUtil.toUpper(javaObjectGenerator.getName()));
                }
                javaObjectSetting.put(name + "_" + "targetPackage", javaObjectGenerator.getTargetPackage());
            }
            globalTemplateArgs.putAll(javaObjectSetting);
            globalTemplateArgs.put("imports", Lists.newArrayList(javaObjectFieldSetting.getImports()));
            globalTemplateArgs.put("fields", javaObjectFieldSetting.getJavaObjectFields());
            globalTemplateArgsContainer.put(domainObjectName, globalTemplateArgs);
        }

        return this;
    }

    private JavaObjectGeneratorConfiguration parseToModelGenerator(Context context) {
        JavaModelGeneratorConfiguration configuration = context.getJavaModelGeneratorConfiguration();
        JavaObjectGeneratorConfiguration mapperJavaObjectGenerator = new JavaObjectGeneratorConfiguration();
        mapperJavaObjectGenerator.setName("model");
        mapperJavaObjectGenerator.setTargetPackage(configuration.getTargetPackage());
        mapperJavaObjectGenerator.setTargetProject(configuration.getTargetProject());
        return mapperJavaObjectGenerator;

    }

    private JavaObjectGeneratorConfiguration parseToMapperGenerator(Context context) {
        JavaClientGeneratorConfiguration configuration =
                context.getJavaClientGeneratorConfiguration();
        JavaObjectGeneratorConfiguration mapperJavaObjectGenerator = new JavaObjectGeneratorConfiguration();
        mapperJavaObjectGenerator.setName("mapper");
        mapperJavaObjectGenerator.setTargetPackage(configuration.getTargetPackage());
        mapperJavaObjectGenerator.setTargetProject(configuration.getTargetProject());
        return mapperJavaObjectGenerator;
    }

    public List<JavaObjectGenerateExecutor> getAllJavaObjectGenerateExecutor() {
        this.resolve();
        Map<String, JavaObjectFieldSetting> javaObjectGenerateSettingMap =
                new HashMap<String, JavaObjectFieldSetting>();
        for (JavaObjectFieldSetting javaObjectFieldSetting : javaObjectFieldSettings) {
            javaObjectGenerateSettingMap.put(javaObjectFieldSetting.getDomainObjectName(), javaObjectFieldSetting);
        }
        List<JavaObjectGenerateExecutor> javaObjectGenerateExecutors = new ArrayList<JavaObjectGenerateExecutor>();
        for (JavaObjectFieldSetting javaObjectFieldSetting : javaObjectFieldSettings) {
            String domainObjectName = javaObjectFieldSetting.getDomainObjectName();
            for (JavaObjectGeneratorConfiguration javaObjectGeneratorConfiguration : javaObjectGeneratorConfigurations) {
                JavaObjectGenerateExecutor javaObjectGenerateExecutor = new JavaObjectGenerateExecutor();
                javaObjectGenerateExecutor
                        .setJavaObjectFieldSetting(javaObjectGenerateSettingMap.get(domainObjectName));
                String prefix = StringUtils.trimToEmpty(javaObjectGeneratorConfiguration.getPrefix());
                if ("model".equals(javaObjectGeneratorConfiguration.getName())) {
                    javaObjectGenerateExecutor.setFullJavaObjectName(prefix + domainObjectName);
                } else {
                    javaObjectGenerateExecutor.setFullJavaObjectName(prefix + domainObjectName +
                            StringCaseUtil.toUpper(javaObjectGeneratorConfiguration.getName()));
                }
                javaObjectGenerateExecutor.setGlobalArgs(globalTemplateArgsContainer.get(domainObjectName));
                javaObjectGenerateExecutor.setTargetPackage(javaObjectGeneratorConfiguration.getTargetPackage());
                javaObjectGenerateExecutor.setTargetProject(javaObjectGeneratorConfiguration.getTargetProject());
                javaObjectGenerateExecutor.setName(javaObjectGeneratorConfiguration.getName());
                javaObjectGenerateExecutor
                        .setTemplateContent(freemarkerTemplateContents.get(javaObjectGeneratorConfiguration.getName()));
                javaObjectGenerateExecutors.add(javaObjectGenerateExecutor);
            }
        }
        return javaObjectGenerateExecutors;
    }
}
