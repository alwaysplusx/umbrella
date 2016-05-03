package com.harmony.umbrella.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.IOUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractRender implements Render {

    protected static final Log LOG = Logs.getLog(Render.class);

    protected boolean flush = false;

    @Override
    public void render(byte[] buf, OutputStream os) throws IOException {
        IOUtils.write(buf, os);
        if (flush) {
            os.flush();
        }
    }

    @Override
    public void render(InputStream is, OutputStream os) throws IOException {
        IOUtils.copy(is, os);
        if (flush) {
            os.flush();
        }
    }

    @Override
    public boolean render(String text, Writer writer) throws IOException {
        IOUtils.write(text, writer);
        if (flush) {
            writer.flush();
        }
        return true;
    }

}
