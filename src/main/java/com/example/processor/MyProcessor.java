package com.example.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * @author bfsan
 * @since 2020/4/22
 **/
@SupportedAnnotationTypes(value = {"com.example.processor.AutoProvided"})
@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {

    public static void main(String[] args) {

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("开始扫描");
        for (TypeElement typeElement : annotations) {
            System.out.println(typeElement);
        }
        System.out.println(roundEnv);
        return true;
    }
}
