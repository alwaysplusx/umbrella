package com.harmony.umbrella.context;

import java.io.IOException;
import java.util.List;

import com.harmony.umbrella.context.ApplicationContext.ClassResource;
import com.harmony.umbrella.context.ApplicationContext.ClassResourceScanner;

public class ClassResourceScannerTest {

    public static void main(String[] args) throws IOException {
        ClassResourceScanner scanner = new ClassResourceScanner();
        List<ClassResource> resources = scanner.scan("classpath*:com/harmony/**/*.class");
        for (ClassResource resource : resources) {
            System.out.println(resource);
        }
    }

}
