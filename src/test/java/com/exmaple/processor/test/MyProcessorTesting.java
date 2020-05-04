package com.exmaple.processor.test;

import com.example.processor.MyProcessor;
import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/**
 * @author bfsan
 * @since 2020/4/25
 **/

public class MyProcessorTesting {

    @Test
    public void testExecute() throws Exception {
        String sourcePath = TestUtils.getResourcePath("TestAutoProvidedBean.txt");
        JavaFileObject javaFileObject = JavaFileObjects
                .forSourceString("TestAutoProvidedBean", TestUtils.getSourceString(sourcePath));
        Truth.assert_().about(javaSource())
                .that(javaFileObject)
                .processedWith(new MyProcessor())
                .compilesWithoutError();
    }

    /*
    JavaFileObject fileObject = JavaFileObjects.forResource("HelloWorld.java");
ASSERT.about(javaSource())
    .that(fileObject)
    .processedWith(new NoHelloWorld())
    .failsToCompile()
    .withErrorContaining("No types named HelloWorld!").in(fileObject).onLine(23).atColumn(5);
     */
}
