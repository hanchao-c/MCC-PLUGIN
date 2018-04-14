package ${REPOSITORY_IMPL_PACKAGE};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ${REPOSITORY_PACKAGE}.${REPOSITORY_NAME};
import ${MODEL_PACKAGE}.${MODEL_NAME};
import ${MAPPER_PACKAGE}.${MAPPER_NAME};

@Repository
public class ${REPOSITORY_NAME}Impl implements ${REPOSITORY_NAME}{

	@Autowired
	private ${MAPPER_NAME} ${MAPPER_LOW_NAME};

	@Override
	public int create(${MODEL_NAME} ${MODEL_LOW_NAME}) {
		return ${MAPPER_LOW_NAME}.insert(${MODEL_LOW_NAME});
	}

	@Override
	public int deleteById(String id) {
		return ${MAPPER_LOW_NAME}.deleteByPrimaryKey(id);
	}

	@Override
	public int update(${MODEL_NAME} ${MODEL_LOW_NAME}) {
		return ${MAPPER_LOW_NAME}.updateByPrimaryKey(${MODEL_LOW_NAME});
	}

	@Override
	public ${MODEL_NAME} getById(String id) {
		return ${MAPPER_LOW_NAME}.selectByPrimaryKey(id);
	}
	
}
