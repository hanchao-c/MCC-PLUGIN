package com.allinmd.generator.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JavaObjectGeneratorConfiguration {

    private String name;
    private String targetPackage;
    private String targetProject;
    private String templatePath;
    private String prefix;

}
