/*
 * 接口的元数据表，包含服务名称，服务地址，服务的用户密码以及一些超时的时间设置
 */
CREATE TABLE UMBRELLA_WS_METADATA
  (
     serviceName        VARCHAR(255) NOT NULL,
     address            VARCHAR(500) NOT NULL,
     username           VARCHAR(50),
     password           VARCHAR(50),
     connectionTimeout  INTEGER default -1,
     receiveTimeout     INTEGER default -1,
     synchronousTimeout INTEGER default -1,
     PRIMARY KEY (serviceName)
  ) 
