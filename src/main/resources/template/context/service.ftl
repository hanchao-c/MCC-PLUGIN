package ${service.targetPackage};

import ${model.targetPackage}.${model.objectName};

public interface ${service.objectName} {

	int create(${model.objectName} ${model.lowObjectName});

	int deleteById(String id);

	int update(${model.objectName} ${model.lowObjectName});

	${model.objectName} getById(String id);

}
