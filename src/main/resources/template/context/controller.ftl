package ${controller.targetPackage};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ${model.targetPackage}.${model.objectName};
import ${service.targetPackage}.${service.objectName};

@RestController
@RequestMapping(value = "${model.lowObjectName}")
public class ${controller.objectName} {

	@Autowired
	private ${service.objectName} ${service.lowObjectName};

	@PostMapping("create")
	public void create(${model.objectName} ${model.lowObjectName}) {
		${service.lowObjectName}.create(${model.lowObjectName});
	}

	@PostMapping("delete")
	public void delete(String id) {
		${service.lowObjectName}.deleteById(id);
	}

	@PostMapping("update")
	public void update(${model.objectName} ${model.lowObjectName}) {
		${service.lowObjectName}.update(${model.lowObjectName});
	}

	@GetMapping("get")
	public ${model.objectName} get${model.lowObjectName}(String id) {
		return ${service.lowObjectName}.getById(id);
	}
	
}
