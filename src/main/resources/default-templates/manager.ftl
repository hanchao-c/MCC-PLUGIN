package ${manager_targetPackage};

import cn.allinmd.common.infrastructure.models.page.PageParam;
import com.allinmd.growth.common.page.SimplePageResult;
import ${DTO_targetPackage}.${DTO_name};
import ${createParam_targetPackage}.${createParam_name};
import ${updateParam_targetPackage}.${updateParam_name};

public interface ${manager_name} {

    int create${model_name}(${createParam_name} ${createParam_name_lower});

    int update${model_name}(${updateParam_name} ${updateParam_name_lower});

${DTO_name} get${model_name}ById(Integer id);

    SimplePageResult<${DTO_name}> get${model_name}PageResult(PageParam pageParam);

}
