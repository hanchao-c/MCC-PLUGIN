package com.allinmd.generator.config;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MybatisGenerateConfig {

    @JacksonXmlProperty(isAttribute = true)
    private String path;
}
