package ${serviceImpl.targetPackage};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ${repository.targetPackage}.${repository.objectName};
import ${model.targetPackage}.${model.objectName};
import ${service.targetPackage}.${service.objectName};

@Service
public class ${serviceImpl.objectName} implements ${service.objectName}{

	@Autowired
	private ${repository.objectName} ${repository.lowObjectName};

	@Override
	public int create(${model.objectName} ${model.lowObjectName}) {
		return ${repository.lowObjectName}.create(${model.lowObjectName});
	}

	@Override
	public int deleteById(String id) {
		return ${repository.lowObjectName}.deleteById(id);
	}

	@Override
	public int update(${model.objectName} ${model.lowObjectName}) {
		return ${repository.lowObjectName}.update(${model.lowObjectName});
	}

	@Override
	public ${model.objectName} getById(String id) {
		return ${repository.lowObjectName}.getById(id);
	}
	
	
	
}
