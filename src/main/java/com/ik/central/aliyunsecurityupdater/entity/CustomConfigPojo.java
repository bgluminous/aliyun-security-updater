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
  /** IP API 返回IP的Json节点名 */
  private String responseJsonIpNodeName;
  /** 重试次数 */
  private Integer retryTimes;
  /** 重试间隔(秒) */
  private Integer retryDelay;
  /** DNS 更新配置 */
  private DomainDNSUpdatePojo[] dns;
  /** ECS 更新配置 */
  private ECSSecurityUpdatePojo[] ecs;
  /** RDS 更新配置 */
  private RDSSecurityUpdatePojo[] rds;

  public String getIpApi() {
    return ipApi == null || ipApi.isEmpty()
      ? "https://api.kiiiv.com/v1/openapi/other/ip"
      : ipApi;
  }

  public String getResponseJsonIpNodeName() {
    return responseJsonIpNodeName == null || responseJsonIpNodeName.isEmpty()
      ? "data"
      : responseJsonIpNodeName;
  }

  public Integer getRetryTimes() {
    return retryTimes == null || retryTimes < 0 ? 0 : retryTimes;
  }

  public Integer getRetryDelay() {
    return retryDelay == null || retryDelay < 0 ? 10 : retryDelay;
  }
}
