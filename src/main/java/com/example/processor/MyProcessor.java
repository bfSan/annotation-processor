package com.example.processor;

import com.google.auto.service.AutoService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
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
        if (roundEnv.processingOver()) {
            return true;
        }
        System.out.println("开始扫描");

        //收集标记注解的element，转换为element entity
        for (Element element : roundEnv.getElementsAnnotatedWith(Autowired.class)) {
            System.out.println(element);
        }

        //为构造方法增加代码内容
        System.out.println(roundEnv);
        return false;
    }
}
