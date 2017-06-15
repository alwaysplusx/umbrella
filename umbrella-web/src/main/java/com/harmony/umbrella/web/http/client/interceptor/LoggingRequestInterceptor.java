package com.harmony.umbrella.web.http.client.interceptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.harmony.umbrella.util.IOUtils;

/**
 * @author wuxii@foxmail.com
 */
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    final static Logger log = LoggerFactory.getLogger(LoggingRequestInterceptor.class);

    private static final AtomicLong ID = new AtomicLong();

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        final long requestId = ID.getAndIncrement();
        if (log.isDebugEnabled()) {
            log.debug("===========================request begin===========================================");
            log.debug("ID           : {}", requestId);
            log.debug("Method       : {}", request.getMethod());
            log.debug("URI          : {}", request.getURI());
            log.debug("Content-Type : {}", request.getHeaders().getContentType());
            log.debug("Headers      : {}", request.getHeaders());
            log.debug("Request body : {}", new String(body, "UTF-8"));
            log.debug("===========================request end=============================================");
        }

        final ClientHttpResponse response = execution.execute(request, body);

        final byte[] responseBody = IOUtils.toByteArray(response.getBody());
        response.close();

        if (log.isDebugEnabled()) {
            log.debug("===========================response begin==========================================");
            log.debug("ID           : {}", requestId);
            log.debug("Status code  : {}", response.getStatusCode());
            log.debug("Status text  : {}", response.getStatusText());
            log.debug("Content-Type : {}", response.getHeaders().getContentType());
            log.debug("Headers      : {}", response.getHeaders());
            log.debug("Response body: {}", new String(responseBody, "UTF-8"));
            log.debug("===========================response end============================================");
        }

        // TODO reset response, after ClientHttpRequest#getBody that body become empty
        return new ClientHttpResponse() {

            private InputStream responseStream = new ByteArrayInputStream(responseBody);

            @Override
            public HttpHeaders getHeaders() {
                return response.getHeaders();
            }

            @Override
            public InputStream getBody() throws IOException {
                return responseStream;
            }

            @Override
            public String getStatusText() throws IOException {
                return response.getStatusText();
            }

            @Override
            public HttpStatus getStatusCode() throws IOException {
                return response.getStatusCode();
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return response.getRawStatusCode();
            }

            @Override
            public void close() {
                try {
                    responseStream.close();
                } catch (IOException e) {
                }
            }
        };
    }

}
