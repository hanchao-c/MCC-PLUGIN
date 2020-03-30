package ${managerImpl_targetPackage};

import cn.allinmd.common.infrastructure.models.page.PageParam;
import com.allinmd.growth.common.page.SimplePageRequest;
import com.allinmd.growth.common.page.SimplePageResult;
import ${manager_targetPackage}.${manager_name};
import com.allinmd.growth.common.page.MybatisPageQueryExecutor;
import ${DTO_targetPackage}.${DTO_name};
import ${mapper_targetPackage}.${mapper_name};
import ${model_targetPackage}.${model_name};
import ${model_targetPackage}.${model_name}Example;
import ${converter_targetPackage}.${converter_name};
import ${createParam_targetPackage}.${createParam_name};
import ${updateParam_targetPackage}.${updateParam_name};
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Log4j2
public class ${managerImpl_name} implements ${manager_name} {

    @Autowired
    private ${mapper_name} ${mapper_name_lower};

    private final ${converter_name} converter = Mappers.getMapper(${converter_name}.class);

    @Override
    public int create${model_name}(${createParam_name} ${createParam_name_lower}) {
${model_name} ${model_name_lower} = converter.convertToPO(${createParam_name_lower});
        return ${mapper_name_lower}.insert(${model_name_lower});
    }

    @Override
    public int update${model_name}(${updateParam_name} ${updateParam_name_lower}) {
${model_name} ${model_name_lower} = converter.convertToPO(${updateParam_name_lower});
        return ${mapper_name_lower}.updateByPrimaryKeySelective(${model_name_lower});
    }

    @Override
    public ${model_name}DTO get${model_name}ById(Integer id) {
${model_name} ${model_name_lower} = ${mapper_name_lower}.selectByPrimaryKey(id);
        return converter.convertToDTO(${model_name_lower});
    }

    @Override
    public SimplePageResult<${DTO_name}> get${model_name}PageResult(PageParam pageParam) {
        SimplePageRequest simplePageRequest = new SimplePageRequest(pageParam);
${model_name}Example example = new ${model_name}Example();
        SimplePageResult<${model_name}> simplePageResult =
            MybatisPageQueryExecutor.execute(${mapper_name_lower}, example, simplePageRequest);
        return simplePageResult.map(converter::convertToDTO);
    }

}
