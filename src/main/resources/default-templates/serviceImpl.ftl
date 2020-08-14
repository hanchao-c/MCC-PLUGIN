package ${serviceImpl_targetPackage};

import cn.allinmd.common.infrastructure.models.page.PageParam;
import cn.allinmd.common.infrastructure.models.page.PageResult;
import ${manager_targetPackage}.${manager_name};
import ${DTO_targetPackage}.${DTO_name};
import ${converter_targetPackage}.${converter_name};
import ${createParam_targetPackage}.${createParam_name};
import ${updateParam_targetPackage}.${updateParam_name};
import ${result_targetPackage}.${result_name};
import ${service_targetPackage}.${service_name};
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class ${serviceImpl_name} implements ${service_name} {

    private final ${converter_name} converter = Mappers.getMapper(${converter_name}.class);

    @Autowired
    private ${manager_name} ${manager_name_lower};

    @Override
    public int create${model_name}(${createParam_name} ${createParam_name_lower}) {
        return ${manager_name_lower}.create${model_name}(${createParam_name_lower});
    }

    @Override
    public void update${model_name}(${updateParam_name} ${updateParam_name_lower}) {
        ${manager_name_lower}.update${model_name}(${updateParam_name_lower});
    }

    @Override
    public ${result_name} get${model_name}ById(Integer id) {
        ${DTO_name} ${DTO_name_lower} = ${manager_name_lower}.get${model_name}ById(id);
        return converter.convertToResult(${DTO_name_lower});
    }

    @Override
    public PageResult<${result_name}> get${model_name}PageResult(PageParam pageParam) {
        SimplePageResult<${DTO_name}> ${model_name_lower}PageResult = ${manager_name_lower}.get${model_name}PageResult(pageParam);
        return ${model_name_lower}PageResult.mapToPageResult(converter::convertToResult);
    }

}
