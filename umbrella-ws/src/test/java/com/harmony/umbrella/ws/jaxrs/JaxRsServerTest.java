package com.harmony.umbrella.ws.jaxrs;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.ws.services.HelloRESTService;

public class JaxRsServerTest {

    private static final String address = "http://localhost:9000";

    @BeforeClass
    public static void setUp() {
        // JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        // sf.setResourceClasses(HelloRESTService.class);
        // sf.setResourceProvider(HelloRESTService.class, new
        // SingletonResourceProvider(new HelloRESTService()));
        // sf.setAddress(address);
        // sf.create();
        JaxRsServerBuilder.create().setAddress(address).publish(HelloRESTService.class);

    }

    @Test
    public void testCall() throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(new HttpGet("http://localhost:9000/hi/wuxii"));
        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("Hi wuxii", EntityUtils.toString(response.getEntity()));
    }

}