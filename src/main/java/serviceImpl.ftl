package ${SERVICE_IMPL_PACKAGE};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ${REPOSITORY_PACKAGE}.${REPOSITORY_NAME};
import ${MODEL_PACKAGE}.${MODEL_NAME};
import ${SERVICE_PACKAGE}.${SERVICE_NAME};

@Service
public class ${SERVICE_NAME}Impl implements ${SERVICE_NAME}{

	@Autowired
	private ${REPOSITORY_NAME} ${REPOSITORY_LOW_NAME};

	@Override
	public int create(${MODEL_NAME} ${MODEL_LOW_NAME}) {
		return ${REPOSITORY_LOW_NAME}.create(${MODEL_LOW_NAME});
	}

	@Override
	public int deleteById(String id) {
		return ${REPOSITORY_LOW_NAME}.deleteById(id);
	}

	@Override
	public int update(${MODEL_NAME} ${MODEL_LOW_NAME}) {
		return ${REPOSITORY_LOW_NAME}.update(${MODEL_LOW_NAME});
	}

	@Override
	public ${MODEL_NAME} getById(String id) {
		return ${REPOSITORY_LOW_NAME}.getById(id);
	}
	
	
	
}
