package com.harmony.umbrella.ws.support;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.harmony.umbrella.ws.Metadata;
import com.harmony.umbrella.ws.MetadataLoader;

/**
 * @author wuxii@foxmail.com
 */
@Remote({ MetadataLoader.class })
@Stateless(mappedName = "DefaultMetadataLoader")
public class DefaultMetadataLoader implements MetadataLoader {

	@Override
	public Metadata loadMetadata(Class<?> serviceClass) {
		return null;
	}

	@Override
	public Metadata loadMetadata(String serviceClassName) {
		return null;
	}

}
