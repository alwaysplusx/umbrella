package com.harmony.umbrella.fs;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.harmony.umbrella.fs.support.FileSystemStorageManager;

/**
 * @author wuxii@foxmail.com
 */
public class FileSystemStorageManagerTest {

    @Test
    public void upload() throws IOException {
        FileSystemStorageManager fssm = new FileSystemStorageManager();
        StorageMetadata sm = fssm.putFile(new File("./pom.xml"));
        System.out.println(sm);
    }

}
