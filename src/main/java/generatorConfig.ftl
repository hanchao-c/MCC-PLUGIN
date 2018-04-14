<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE generatorConfiguration PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN" "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd" >
<generatorConfiguration>
	<context id="Mysql" targetRuntime="MyBatis3">
		<property name="javaFileEncoding" value="UTF-8"/>
		<plugin type="org.mybatis.generator.plugins.SerializablePlugin"/>
		<plugin type="org.mybatis.generator.plugins.EqualsHashCodePlugin"/>
		<plugin type="org.mybatis.generator.plugins.CaseInsensitiveLikePlugin"/>
		<plugin type="org.mybatis.generator.plugins.ToStringPlugin"/>
		<plugin type="tk.mybatis.mapper.generator.MapperPlugin">
			<property name="mappers" value="tk.mybatis.mapper.common.Mapper"/>
			<property name="javaFileEncoding" value="UTF-8"/>  
		</plugin>
		<commentGenerator>
			<property name="suppressAllComments" value="true" />
			<property name="suppressDate" value="true" />
		</commentGenerator>
		<jdbcConnection driverClass="com.mysql.jdbc.Driver" 
						connectionURL="${connectionURL}"
						userId="${userId}" 
						password="${password}"/>
		<javaTypeResolver>
			<property name="forceBigDecimals" value="true" />
		</javaTypeResolver>
		<javaModelGenerator targetPackage="${modelPackage}" targetProject="src\main\java">
			<property name="enableSubPackages" value="true" />
			<property name="trimStrings" value="true" />
		</javaModelGenerator>
		<sqlMapGenerator targetPackage="${mappingPackage}" targetProject="src\main\java">
			<property name="enableSubPackages" value="true" />
		</sqlMapGenerator>
		<javaClientGenerator targetPackage="${mapperPackage}" targetProject="src\main\java" type="XMLMAPPER">
			<property name="enableSubPackages" value="true" />
		</javaClientGenerator>
		
 		<#list tables as table><table schema="${schema}" tableName="${table.tableName}" domainObjectName="${table.domainObjectName}"  enableCountByExample="false"/></#list> 
 		
	</context>
</generatorConfiguration>