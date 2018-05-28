package ${mock.targetPackage};
<#assign bigDecimalImport="false"/>
<#assign dateImport="false"/> 
import ${model.targetPackage}.${model.objectName};
<#list introspectedColumns as column>
	<#if (column.fullyQualifiedJavaType.shortName=='BigDecimal' && bigDecimalImport =='false') >
	<#assign bigDecimalImport="true"/>
import java.math.BigDecimal;
	<#elseif (column.fullyQualifiedJavaType.shortName=='Date' && dateImport =='false')>
	<#assign dateImport="true"/>
import java.util.Date;
	<#else>
	</#if>
</#list>
import javax.validation.constraints.Size;

public class ${mock.objectName} extends ${model.objectName} {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	<#list introspectedColumns as column>
	<#if column.fullyQualifiedJavaType.shortName=='String'>
	@Size(max = ${column.length}, message = "${column.remarks}字符长度不能超出${column.length}")
	</#if>
	@Override
	public ${column.fullyQualifiedJavaType.shortName} get${column.javaProperty}() {
		return super.get${column.javaProperty}();
	}
	
	</#list>
}
