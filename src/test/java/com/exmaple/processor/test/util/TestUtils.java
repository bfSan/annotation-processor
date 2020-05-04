package com.exmaple.processor.test.util;

import com.exmaple.processor.test.MyProcessorTesting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Objects;

/**
 * @author bfsan
 * @since 2020/5/4
 **/
public final class TestUtils {

    private TestUtils() {
        throw new UnsupportedOperationException("工具类不能初始化");
    }

    public static String getResourcePath(String name) {
        URL url = MyProcessorTesting.class.getClassLoader().getResource("TestAutoProvidedBean.txt");
        return Objects.requireNonNull(url).getPath();
    }

    public static String getSourceString(String sourcePath) throws Exception {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(sourcePath)))) {
            String s = "";
            while ((s = reader.readLine()) != null) {
                builder.append(s).append("\n");
            }
        }
        return builder.toString();
    }
}
