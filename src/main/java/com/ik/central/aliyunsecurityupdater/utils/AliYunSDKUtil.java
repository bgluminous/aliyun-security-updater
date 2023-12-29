package com.ik.central.aliyunsecurityupdater.utils;

import com.aliyun.teaopenapi.models.Config;
import com.ik.central.aliyunsecurityupdater.ConfigInstance;
import com.ik.central.aliyunsecurityupdater.entity.CustomConfigPojo;
import com.ik.central.aliyunsecurityupdater.exception.ConfigRuntimeException;

public class AliYunSDKUtil {

  private static final String ERR = "创建Client设置参数校验失败! 错误信息: %s";

  private static final CustomConfigPojo config = ConfigInstance.getConfig();

  private AliYunSDKUtil() {
  }

  public static com.aliyun.ecs20140526.Client getECSClient(String endpoint) {
    try {
      return new com.aliyun.ecs20140526.Client(
        new Config()
          .setAccessKeyId(config.getAccessKeyId())
          .setAccessKeySecret(config.getAccessKeySecret())
          .setEndpoint(endpoint));
    } catch (Exception ex) {
      throw new ConfigRuntimeException(ERR.formatted(ex));
    }
  }

  public static com.aliyun.rds20140815.Client getRDSClient(String endpoint) {
    try {
      return new com.aliyun.rds20140815.Client(
        new Config()
          .setAccessKeyId(config.getAccessKeyId())
          .setAccessKeySecret(config.getAccessKeySecret())
          .setEndpoint(endpoint));
    } catch (Exception ex) {
      throw new ConfigRuntimeException(ERR.formatted(ex));
    }
  }

  public static com.aliyun.alidns20150109.Client getDNSClient(String endpoint) {
    try {
      return new com.aliyun.alidns20150109.Client(
        new Config()
          .setAccessKeyId(config.getAccessKeyId())
          .setAccessKeySecret(config.getAccessKeySecret())
          .setEndpoint(endpoint));
    } catch (Exception ex) {
      throw new ConfigRuntimeException(ERR.formatted(ex));
    }
  }
}
