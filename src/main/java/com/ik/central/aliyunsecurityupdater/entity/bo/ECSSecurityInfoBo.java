package com.ik.central.aliyunsecurityupdater.entity.bo;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class ECSSecurityInfoBo {
  /** 安全组名称 */
  private String securityGroupName;
  /** 安全组规则ID */
  private String securityGroupRuleId;
  /** IP */
  private String currentIp;
}
