package ${service_targetPackage};

import cn.allinmd.common.infrastructure.models.page.PageParam;
import cn.allinmd.common.infrastructure.models.page.PageResult;
import ${createParam_targetPackage}.${createParam_name};
import ${updateParam_targetPackage}.${updateParam_name};
import ${result_targetPackage}.${result_name};


public interface ${service_name} {

    int create${model_name}(${createParam_name} ${createParam_name});

    void update${model_name}(${updateParam_name} ${updateParam_name});

${result_name} get${model_name}ById(Integer id);

    PageResult<${result_name}> get${model_name}PageResult(PageParam pageParam);

 }
