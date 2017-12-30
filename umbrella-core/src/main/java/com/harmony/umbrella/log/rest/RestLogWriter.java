package com.harmony.umbrella.log.rest;

import java.util.Arrays;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.support.AbstractBufferedLogWriter;

/**
 * @author wuxii@foxmail.com
 */
public class RestLogWriter extends AbstractBufferedLogWriter {

    private RestTemplate restTemplate;

    private String url;

    private Converter<LogInfo, RestLogMessage> converter;

    public RestLogWriter() {
        super(0);
    }

    public RestLogWriter(int bufferSize) {
        super(bufferSize);
    }

    public void write(RestLogMessage msg) {
        write(new RestLogMessage[] { msg });
    }

    public void write(List<RestLogMessage> msg) {
        write(msg.toArray(new RestLogMessage[msg.size()]));
    }

    public void write(RestLogMessage... msg) {
        ResponseEntity<String> response = restTemplate.postForEntity(url, msg, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RestClientException("response status code " + response.getStatusCodeValue());
        }
    }

    @Override
    protected void writeInternal(LogInfo info) {
        writeInternal(Arrays.asList(info));
    }

    @Override
    protected void writeInternal(List<LogInfo> infos) {
        RestLogMessage[] msg = new RestLogMessage[infos.size()];
        for (int i = 0, max = infos.size(); i < max; i++) {
            LogInfo info = infos.get(i);
            if (converter != null) {
                msg[i] = converter.convert(info);
            } else {
                msg[i] = RestLogMessage.convert(info);
            }
        }
        write(msg);
    }

    public Converter<LogInfo, RestLogMessage> getConverter() {
        return converter;
    }

    public void setConverter(Converter<LogInfo, RestLogMessage> converter) {
        this.converter = converter;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
