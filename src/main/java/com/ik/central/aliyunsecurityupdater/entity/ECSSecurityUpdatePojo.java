package com.ik.central.aliyunsecurityupdater.entity;

import lombok.Data;

@Data
public class ECSSecurityUpdatePojo {
  /** 阿里云地域ID */
  private String endpoint;
  /** 地域ID */
  private String regionId;
  /** 安全组ID */
  private String securityGroupId;
  /** 规则描述 */
  private String description;
}
