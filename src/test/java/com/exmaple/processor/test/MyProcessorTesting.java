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
    public void testExecute() {
        JavaFileObject javaFileObject = JavaFileObjects.forSourceLines("TestAutoProvidedBean",
                "import com.example.processor.AutoProvided;\n",
                "import org.springframework.beans.factory.annotation.Autowired;\n",
                "import org.springframework.context.ApplicationContext;",
                "@AutoProvided\n",
                "public class TestAutoProvidedBean {\n",
                "    @Autowired\n",
                "    private ApplicationContext applicationContext;\n",
                "    public void helloWorld() {\n",
                "        System.out.println(\"say\");\n",
                "    }\n",
                "}");
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
