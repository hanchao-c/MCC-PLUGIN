package com.allinmd.generator.util;

import com.allinmd.generator.resource.ClassPathResource;
import com.google.common.base.Throwables;

import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

public class ValidateXML {

    public static void validateGeneratorXML(Source source) {
        ClassPathResource xsdPath = new ClassPathResource("generator.xsd");
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema = schemaFactory.newSchema(xsdPath.getAsURL());
            Validator validator = schema.newValidator();
            validator.validate(source);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

}
