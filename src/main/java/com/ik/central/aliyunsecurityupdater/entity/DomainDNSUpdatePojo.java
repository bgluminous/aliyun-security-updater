package com.ik.central.aliyunsecurityupdater.entity;

import lombok.Data;

@Data
public class DomainDNSUpdatePojo {
  /** 阿里云地域ID */
  private String endpoint;
  /** Domain */
  private String domainName;
  /** RR记录 */
  private String rr;
}
