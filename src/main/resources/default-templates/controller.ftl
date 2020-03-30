package ${controller_targetPackage};


import cn.allinmd.common.infrastructure.models.page.PageParam;
import cn.allinmd.common.infrastructure.models.page.PageResult;
import cn.allinmd.common.infrastructure.models.response.WebResponse;
import cn.allinmd.common.infrastructure.models.response.WebResponseUtils;
import ${updateParam_targetPackage}.${updateParam_name};
import ${createParam_targetPackage}.${createParam_name};
import ${result_targetPackage}.${result_name};
import ${service_targetPackage}.${service_name};
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.log4j.Log4j2;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("${model_name_lower}")
@Log4j2
public class ${controller_name} {

    @Autowired
    private ${service_name} ${service_name_lower};

    @PostMapping("create${model_name}")
    public WebResponse create${model_name}(@Validated @RequestBody ${createParam_name} ${createParam_name_lower}) {
${service_name_lower}.create${model_name}(${createParam_name_lower});
        return WebResponseUtils.successWebResponse(null);
    }

    @PostMapping("update${model_name}")
    public WebResponse Update${model_name}(@Validated @RequestBody ${updateParam_name} ${updateParam_name_lower}) {
${service_name_lower}.update${model_name}(${updateParam_name_lower});
        return WebResponseUtils.successWebResponse(null);
    }

    @GetMapping("get${model_name}ById")
    public WebResponse<${result_name}> get${model_name}ById(@NotNull Integer id) {
${result_name} ${result_name_lower} = ${service_name_lower}.get${model_name}ById(id);
        return WebResponseUtils.successWebResponse(${result_name_lower});
    }

    @GetMapping("get${model_name}Page")
    public WebResponse
<PageResult<${result_name}>> get${model_name}Page(PageParam pageParam) {
        PageResult<${result_name}> ${model_name_lower}PageResult = ${service_name_lower}.get${model_name}PageResult(pageParam);
        return WebResponseUtils.successWebResponse(${model_name_lower}PageResult);
    }

}
