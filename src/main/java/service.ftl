package ${SERVICE_PACKAGE};

import ${MODEL_PACKAGE}.${MODEL_NAME};

public interface ${SERVICE_NAME} {

	int create(${MODEL_NAME} ${MODEL_LOW_NAME});

	int deleteById(String id);

	int update(${MODEL_NAME} ${MODEL_LOW_NAME});

	${MODEL_NAME} getById(String id);

}
