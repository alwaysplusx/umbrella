package com.harmony.umbrella.fs;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;

import org.junit.Ignore;
import org.junit.Test;

import com.harmony.umbrella.fs.support.FileSystemStorageManager;
import com.harmony.umbrella.json.Json;

/**
 * @author wuxii@foxmail.com
 */
@Ignore
public class StorageManagerTest {

    public static void main(String[] args) throws IOException {
        System.out.println(new File("/umbrella-ss").getAbsolutePath());
        System.out.println(Inet4Address.getLocalHost().getHostAddress());
    }

    @Test
    public void testFileStorage() throws IOException {
        FileSystemStorageManager fssm = new FileSystemStorageManager();
        StorageMetadata sm = fssm.putFile(new File("pom.xml"));
        System.out.println(Json.toJson(sm));
    }

}
