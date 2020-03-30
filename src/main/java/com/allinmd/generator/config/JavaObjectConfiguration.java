package com.allinmd.generator.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JavaObjectConfiguration {

    private List<Property> properties;

    private List<JavaObjectGeneratorConfiguration> javaObjectGenerators;

    private MybatisGenerateConfig mybatisGenerateConfig;

}
