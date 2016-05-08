package com.harmony.umbrella.log;

import com.harmony.umbrella.log.annotation.Logging;

/**
 * @author wuxii@foxmail.com
 */
public interface SampleService {

    @Logging(module = "测试模块", action = "保存", message = "对数据Sample[{0.sampleId}]进行保存操作，操作结果为：{result}")
    public String save(SampleEntity entity);

}
