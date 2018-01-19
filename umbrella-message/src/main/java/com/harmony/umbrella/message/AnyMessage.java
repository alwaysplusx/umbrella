package com.harmony.umbrella.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.springframework.util.ClassUtils;

/**
 * @author wuxii@foxmail.com
 */
public class AnyMessage implements Serializable {

    private static final long serialVersionUID = -1157340487713663489L;

    private byte[] buf;
    private String type;

    public AnyMessage() {
    }

    public AnyMessage(Serializable o) {
        this.set(o);
    }

    public Object get() {
        return get(Object.class);
    }

    public <T> T get(Class<T> type) {
        if (buf == null) {
            return null;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (T) ois.readObject();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void set(Serializable o) {
        String oType = this.type;
        byte[] oBuf = this.buf;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(o);
            this.type = o.getClass().getName();
            this.buf = baos.toByteArray();
        } catch (IOException e) {
            type = oType;
            buf = oBuf;
            throw new IllegalStateException(e);
        }
    }

    public String getType() {
        return type;
    }

    public byte[] toBufferArray() {
        if (buf == null) {
            return null;
        }
        byte[] o = new byte[buf.length];
        System.arraycopy(buf, 0, o, 0, buf.length);
        return o;
    }

    public Class<?> forType() {
        return forType(ClassUtils.getDefaultClassLoader());
    }

    public Class<?> forType(ClassLoader loader) {
        if (type != null) {
            try {
                return Class.forName(type, false, loader);
            } catch (Throwable e) {
            }
        }
        return null;
    }
}
