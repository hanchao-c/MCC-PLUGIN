package com.allinmd.generator.exe;

import com.allinmd.generator.config.JavaObjectConfiguration;
import com.google.common.base.Throwables;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
@ToString
public class TemplateSettingsProcessor {

    private JavaObjectConfiguration javaObjectConfiguration;

    private Configuration mybatisConfiguration;

    public TemplateSettings process() {
        TemplateSettings templateSettings = new TemplateSettings();
        List<Context> contexts = this.mybatisConfiguration.getContexts();
        Context context = contexts.get(0);

        List<IntrospectedTable> introspectedTables = getIntrospectedTables(context);
        List<JavaObjectFieldSetting> javaObjectFieldSettings = new ArrayList<JavaObjectFieldSetting>();
        for (IntrospectedTable introspectedTable : introspectedTables) {
            JavaObjectFieldSetting javaObjectFieldSetting = new JavaObjectFieldSetting();
            List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
            List<JavaObjectField> fields = new ArrayList<JavaObjectField>();
            for (IntrospectedColumn introspectedColumn : allColumns) {
                JavaObjectField javaObjectField = new JavaObjectField();
                javaObjectField.setFieldName(introspectedColumn.getJavaProperty());
                javaObjectField.setRemarks(introspectedColumn.getRemarks());
                FullyQualifiedJavaType fullyQualifiedJavaType =
                        introspectedColumn.getFullyQualifiedJavaType();
                javaObjectField.setFullFieldType(fullyQualifiedJavaType.getFullyQualifiedName());
                javaObjectField
                        .setFieldType(
                                fullyQualifiedJavaType.getFullyQualifiedNameWithoutTypeParameters());
                javaObjectField.setFieldType(fullyQualifiedJavaType.getShortName());
                fields.add(javaObjectField);
            }
            Set<String> imports = new TreeSet<String>();
            for (JavaObjectField javaObjectField : fields) {
                imports.add(javaObjectField.getFullFieldType());
            }
            javaObjectFieldSetting.setImports(imports);
            javaObjectFieldSetting.setDomainObjectName(introspectedTable.getTableConfiguration().getDomainObjectName());
            javaObjectFieldSetting.setJavaObjectFields(fields);
            javaObjectFieldSettings.add(javaObjectFieldSetting);
        }
        templateSettings.setJavaObjectFieldSettings(javaObjectFieldSettings);
        System.err.println(javaObjectFieldSettings);
        templateSettings.setProperties(javaObjectConfiguration.getProperties());
        templateSettings.setJavaObjectGeneratorConfigurations(javaObjectConfiguration.getJavaObjectGenerators());
        templateSettings.setContext(context);
        return templateSettings;
    }

    private List<IntrospectedTable> getIntrospectedTables(Context context) {
        try {
            Field declaredField = context.getClass().getDeclaredField("introspectedTables");
            declaredField.setAccessible(true);
            return (List<IntrospectedTable>) (declaredField.get(context));
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

}
