package com.harmony.umbrella.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author wuxii@foxmail.com
 */
public class AnyMessage implements Serializable {

    public static final String ANY_MESSAGE_HEADER = AnyMessage.class + ".type";

    private static final long serialVersionUID = -1157340487713663489L;

    private byte[] buf;
    private String type;

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

    public void set(Object o) {
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

    public byte[] getBufferArray() {
        return buf;
    }

}
