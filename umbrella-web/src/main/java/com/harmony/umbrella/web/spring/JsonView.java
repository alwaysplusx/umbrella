package com.harmony.umbrella.web.spring;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.AbstractView;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.data.util.DataUtils;

/**
 * @author wuxii@foxmail.com
 */
public class JsonView extends AbstractView {

    public static final String DEFAULT_CONTENT_TYPE = "application/json";

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    /**
     * 渲染的字符集
     */
    private Charset charset;

    private boolean disableCaching = true;

    private boolean updateContentLength = false;

    private boolean extractValueFromSingleKeyModel = false;

    private Set<String> renderedAttributes;

    private Set<SerializerFeature> serializerFeatures = new HashSet<SerializerFeature>();

    private Set<String> excludes = new HashSet<String>();

    public JsonView() {
        this(DEFAULT_CHARSET);
    }

    public JsonView(Charset charset) {
        setContentType(DEFAULT_CONTENT_TYPE);
        setExposePathVariables(false);
        this.charset = charset == null ? DEFAULT_CHARSET : charset;
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Object value = filterModel(model);

        String text = DataUtils.toJson(value, //
                serializerFeatures.toArray(new SerializerFeature[serializerFeatures.size()]),//
                excludes.toArray(new String[excludes.size()]));

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

    protected Object filterModel(Map<String, Object> model) {
        Map<String, Object> result = new HashMap<String, Object>(model.size());
        Set<String> renderedAttributes = !CollectionUtils.isEmpty(this.renderedAttributes) ? this.renderedAttributes : model.keySet();
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            if (!(entry.getValue() instanceof BindingResult) && renderedAttributes.contains(entry.getKey())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        if (extractValueFromSingleKeyModel) {
            if (result.size() == 1) {
                for (Map.Entry<String, Object> entry : result.entrySet()) {
                    return entry.getValue();
                }
            }
        }
        return result;
    }

    public Set<SerializerFeature> getSerializerFeatures() {
        return serializerFeatures;
    }

    public void setSerializerFeatures(Set<SerializerFeature> serializerFeatures) {
        this.serializerFeatures = serializerFeatures;
    }

    public Set<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(Set<String> excludes) {
        this.excludes = excludes;
    }

    public void addExcludes(String... excludes) {
        for (String name : excludes) {
            this.excludes.add(name);
        }
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

    public Set<String> getRenderedAttributes() {
        return renderedAttributes;
    }

    public void setRenderedAttributes(Set<String> renderedAttributes) {
        this.renderedAttributes = renderedAttributes;
    }
}
