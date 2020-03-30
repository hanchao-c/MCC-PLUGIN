package com.allinmd.generator.config;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Property {

    private String name;
    private String value;


    public void valid() {
        if (null == name || null == value) {
            throw new NullPointerException();
        }
    }
}
