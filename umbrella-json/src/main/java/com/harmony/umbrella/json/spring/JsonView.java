package com.harmony.umbrella.json.spring;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author wuxii@foxmail.com
 */
public abstract class JsonView extends AbstractView {

    public static final String DEFAULT_CONTENT_TYPE = "application/json";

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    /**
     * 渲染的字符集
     */
    private Charset charset;

    private boolean disableCaching = true;

    private boolean updateContentLength = false;

    private boolean extractValueFromSingleKeyModel = false;

    private Set<SerializerFeature> serializerFeatures = new HashSet<SerializerFeature>();

    public JsonView() {
        this(DEFAULT_CHARSET);
    }

    public JsonView(Charset charset) {
        setContentType(DEFAULT_CONTENT_TYPE);
        setExposePathVariables(false);
        this.charset = charset == null ? DEFAULT_CHARSET : charset;
    }

    protected abstract String toJsonString(Map<String, Object> model, SerializerFeature[] SerializerFeature);

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String text = toJsonString(model, serializerFeatures.toArray(new SerializerFeature[serializerFeatures.size()]));

        byte[] bytes = text.getBytes(charset);

        OutputStream stream = this.updateContentLength ? createTemporaryOutputStream() : response.getOutputStream();
        stream.write(bytes);

        if (this.updateContentLength) {
            writeToResponse(response, (ByteArrayOutputStream) stream);
        }
    }

    @Override
    protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        setResponseContentType(request, response);
        response.setCharacterEncoding(charset.name());
        if (this.disableCaching) {
            response.addHeader("Pragma", "no-cache");
            response.addHeader("Cache-Control", "no-cache, no-store, max-age=0");
            response.addDateHeader("Expires", 1L);
        }
    }

    public Set<SerializerFeature> getSerializerFeatures() {
        return serializerFeatures;
    }

    public void setSerializerFeatures(Set<SerializerFeature> serializerFeatures) {
        this.serializerFeatures = serializerFeatures;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public boolean isDisableCaching() {
        return disableCaching;
    }

    public void setDisableCaching(boolean disableCaching) {
        this.disableCaching = disableCaching;
    }

    public boolean isUpdateContentLength() {
        return updateContentLength;
    }

    public void setUpdateContentLength(boolean updateContentLength) {
        this.updateContentLength = updateContentLength;
    }

    public boolean isExtractValueFromSingleKeyModel() {
        return extractValueFromSingleKeyModel;
    }

    public void setExtractValueFromSingleKeyModel(boolean extractValueFromSingleKeyModel) {
        this.extractValueFromSingleKeyModel = extractValueFromSingleKeyModel;
    }
}
