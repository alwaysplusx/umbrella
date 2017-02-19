package com.harmony.umbrella.data.vo;

/**
 * @author wuxii@foxmail.com
 */
public class ModelVo {

    private long size;
    private String name;
    private String code;
    private String content;

    public ModelVo() {
    }

    public ModelVo(long size, String name, String content) {
        this.size = size;
        this.name = name;
        this.content = content;
    }

    public ModelVo(String name, String code, String content) {
        this.name = name;
        this.code = code;
        this.content = content;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "{size:" + size + ", name:" + name + ", code:" + code + ", content:" + content + "}";
    }

}
