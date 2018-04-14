package ${CONTROLLER_PACKAGE};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ${MODEL_PACKAGE}.${MODEL_NAME};
import ${SERVICE_PACKAGE}.${SERVICE_NAME};

@RestController
@RequestMapping(value = "${MODEL_LOW_NAME}", produces = "application/json;charset=UTF-8")
public class ${MODEL_NAME}Controller {

	@Autowired
	private ${SERVICE_NAME} ${SERVICE_LOW_NAME};

	@PostMapping("create")
	public void create(${MODEL_NAME} ${MODEL_LOW_NAME}) {
		${SERVICE_LOW_NAME}.create(${MODEL_LOW_NAME});
	}

	@PostMapping("delete")
	public void delete(String id) {
		${SERVICE_LOW_NAME}.deleteById(id);
	}

	@PostMapping("update")
	public void update(${MODEL_NAME} ${MODEL_LOW_NAME}) {
		${SERVICE_LOW_NAME}.update(${MODEL_LOW_NAME});
	}

	@GetMapping("get")
	public ${MODEL_NAME} get${MODEL_NAME}(String id) {
		return ${SERVICE_LOW_NAME}.getById(id);
	}
	
}
