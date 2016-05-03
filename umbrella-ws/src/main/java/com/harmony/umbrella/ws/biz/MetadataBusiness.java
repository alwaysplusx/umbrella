package com.harmony.umbrella.ws.biz;

import com.harmony.umbrella.biz.Business;
import com.harmony.umbrella.ws.Context;
import com.harmony.umbrella.ws.MetadataLoader;
import com.harmony.umbrella.ws.persistence.MetadataEntity;

/**
 * @author wuxii@foxmail.com
 */
public interface MetadataBusiness extends Business<MetadataEntity, String>, MetadataLoader {

    Context reset(Context context);

}
