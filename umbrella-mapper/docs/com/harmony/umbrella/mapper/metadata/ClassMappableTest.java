package com.harmony.umbrella.mapper.metadata;

import com.harmony.umbrella.mapper.metadata.impl.ClassMappableImpl;
import com.harmony.umbrella.mapper.vo.A;

/**
 * @author wuxii@foxmail.com
 */
public class ClassMappableTest {

	public static void main(String[] args) {
		ClassMappable cm = new ClassMappableImpl(A.class);
		System.out.println(cm);
	}

}
