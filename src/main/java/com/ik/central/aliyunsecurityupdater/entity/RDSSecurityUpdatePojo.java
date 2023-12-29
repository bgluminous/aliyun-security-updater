package com.ik.central.aliyunsecurityupdater.entity;

import lombok.Data;

@Data
public class RDSSecurityUpdatePojo {
  /** 阿里云地域ID */
  private String endpoint;
  /** RDS实例ID */
  private String dbInstanceId;
  /** 白名单组名 */
  private String ipArrayName;
}
