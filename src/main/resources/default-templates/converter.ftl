package ${converter_targetPackage};

import ${DTO_targetPackage}.${DTO_name};
import ${model_targetPackage}.${model_name};
import ${createParam_targetPackage}.${createParam_name};
import ${updateParam_targetPackage}.${updateParam_name};
import ${result_targetPackage}.${result_name};
import org.mapstruct.Mapper;

@Mapper
public interface ${converter_name} {

    ${model_name} convertToPO(${createParam_name} ${createParam_name_lower});

    ${model_name} convertToPO(${updateParam_name} ${updateParam_name_lower});

    ${DTO_name} convertToDTO(${model_name} ${model_name_lower});

    ${result_name} convertToResult(${DTO_name} ${DTO_name_lower});

}
