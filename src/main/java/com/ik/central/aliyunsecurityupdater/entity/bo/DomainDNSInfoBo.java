package com.ik.central.aliyunsecurityupdater.entity.bo;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class DomainDNSInfoBo {
  /** 记录ID */
  private String recordId;
  /** IP */
  private String currentIp;
}
