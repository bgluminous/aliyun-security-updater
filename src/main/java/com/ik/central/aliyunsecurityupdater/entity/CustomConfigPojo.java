package com.ik.central.aliyunsecurityupdater.entity;

import lombok.Data;

@Data
public class CustomConfigPojo {
  /** accessKeyId */
  private String accessKeyId;
  /** accessKeySecret */
  private String accessKeySecret;
  /** cron */
  private String cron;
  /** IP API */
  private String ipApi;
  /** DNS 更新配置 */
  private DomainDNSUpdatePojo[] dns;
  /** ECS 更新配置 */
  private ECSSecurityUpdatePojo[] ecs;
  /** RDS 更新配置 */
  private RDSSecurityUpdatePojo[] rds;
}
