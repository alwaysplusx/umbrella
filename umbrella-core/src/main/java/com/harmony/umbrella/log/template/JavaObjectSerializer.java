package com.harmony.umbrella.log.template;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.harmony.umbrella.core.ObjectSerializer;

/**
 * @author wuxii@foxmail.com
 */
class JavaObjectSerializer implements ObjectSerializer {

    @Override
    public Object serialize(Object val) {
        if (val == null) {
            return null;
        }
        if (!(val instanceof Serializable)) {
            throw new IllegalArgumentException(val + " not a serializable object");
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(val);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            return ois.readObject();
        } catch (IOException e) {
            throw new IllegalStateException("can not serialize object " + val, e);
        } catch (ClassNotFoundException e) {
            // ignore
        }
        return val;
    }

}
