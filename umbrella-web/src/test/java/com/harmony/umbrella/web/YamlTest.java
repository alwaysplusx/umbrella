package com.harmony.umbrella.web;

import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;

/**
 * @author wuxii@foxmail.com
 */
public class YamlTest {

    public static void main(String[] args) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.yml");
        Yaml yaml = new Yaml();
        Object obj = yaml.load(is);
        System.out.println(obj);
    }

}
