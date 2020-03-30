package com.allinmd.generator.exe;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
public class JavaObjectFieldSetting {

    private String domainObjectName;
    private Set<String> imports;
    private List<JavaObjectField> javaObjectFields;
}
