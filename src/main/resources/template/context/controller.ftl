package ${controller.targetPackage};
<#assign mockIn="false"/>
<#if mock??>
<#assign mockIn="true"/>
</#if> 
import org.springframework.beans.factory.annotation.Autowired;
<#if mockIn == 'true'>
import org.springframework.validation.annotation.Validated;
</#if> 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
<#if mockIn == 'true'>
import ${mock.targetPackage}.${mock.objectName};
</#if>  
import ${model.targetPackage}.${model.objectName};
import ${service.targetPackage}.${service.objectName};

@RestController
@RequestMapping(value = "${model.lowObjectName}")
public class ${controller.objectName} {

	@Autowired
	private ${service.objectName} ${service.lowObjectName};

	@PostMapping("create")
	public void create(<#if mockIn == 'true'>@Validated ${mock.objectName}<#else>${model.objectName}</#if>  <#if mockIn == 'true'>${mock.lowObjectName}<#else>${model.lowObjectName}</#if>) {
		${service.lowObjectName}.create(<#if mockIn == 'true'>${mock.lowObjectName}<#else>${model.lowObjectName}</#if>);
	}

	@PostMapping("delete")
	public void delete(String id) {
		${service.lowObjectName}.deleteById(id);
	}

	@PostMapping("update")
	public void update(<#if mockIn == 'true'>@Validated ${mock.objectName}<#else>${model.objectName}</#if>  <#if mockIn == 'true'>${mock.lowObjectName}<#else>${model.lowObjectName}</#if>) {
		${service.lowObjectName}.update(<#if mockIn == 'true'>${mock.lowObjectName}<#else>${model.lowObjectName}</#if>);
	}

	@GetMapping("get")
	public ${model.objectName} get${model.lowObjectName}(String id) {
		return ${service.lowObjectName}.getById(id);
	}
	
}
