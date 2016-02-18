package com.harmony.umbrella.log.annotation;

import com.harmony.umbrella.log.Level;

public @interface LogBiz {

    String module() default "";

    String action() default "";

    Level level() default Level.INFO;

    /**
     * 业务日志对应的entity
     * 
     * @return
     */
    Entity[] entitys() default {};

    Class<?> entityClass() default Void.class;

    int[] idPosition() default {};

    public @interface Entity {

        /**
         * entity对应的类
         * 
         * @return
         */
        Class<?> entityClass();

        /**
         * 业务数据对应的唯一键id名称
         * 
         * @return
         */
        String[] idNames() default {};

        /**
         * entity对应参数所在的位置. default -1 表示自动匹配找到的第一个(index从0开始)
         * 
         * @return
         */
        int position() default -1;

    }
}