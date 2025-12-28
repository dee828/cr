package com.example.generator;

import com.example.generator.util.CustomFreemarkerUtil;

import java.io.File;
import java.util.HashMap;

public class GeneratorDemo {

    static String targetPath = "generator/src/main/java/com/example/generator/demo/";

    static {
        new File(targetPath).mkdirs();
    }

    public static void main(String[] args) throws Exception {
        // 模版
        CustomFreemarkerUtil.getTemplate("test.ftl");

        // 数据
        HashMap<String, Object> data = new HashMap<>();
        String className = "Test4";
        data.put("className", className);

        CustomFreemarkerUtil.generate(targetPath + className + ".java", data);
    }
}
