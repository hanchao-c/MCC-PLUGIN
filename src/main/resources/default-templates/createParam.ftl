package ${createParam_targetPackage};

<#list imports as import>
import ${import};
</#list>

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ${createParam_name} {

<#list fields as field>
    /**
     * ${field.remarks}
     */
    private ${field.fieldType} ${field.fieldName};
</#list>

}
