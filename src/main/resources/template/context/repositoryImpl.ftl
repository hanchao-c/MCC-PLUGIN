package ${repositoryImpl.targetPackage};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ${repository.targetPackage}.${repository.objectName};
import ${model.targetPackage}.${model.objectName};
import ${mapper.targetPackage}.${mapper.objectName};

@Repository
public class ${repositoryImpl.objectName} implements ${repository.objectName}{

	@Autowired
	private ${mapper.objectName} ${mapper.lowObjectName};

	@Override
	public int create(${model.objectName} ${model.lowObjectName}) {
		return ${mapper.lowObjectName}.insert(${model.lowObjectName});
	}

	@Override
	public int deleteById(String id) {
		return ${mapper.lowObjectName}.deleteByPrimaryKey(id);
	}

	@Override
	public int update(${model.objectName} ${model.lowObjectName}) {
		return ${mapper.lowObjectName}.updateByPrimaryKey(${model.lowObjectName});
	}

	@Override
	public ${model.objectName} getById(String id) {
		return ${mapper.lowObjectName}.selectByPrimaryKey(id);
	}
	
}
