package com.ik.central.aliyunsecurityupdater.service;

import com.aliyun.ecs20140526.Client;
import com.aliyun.ecs20140526.models.*;
import com.heavenark.infrastructure.log.LogFactory;
import com.heavenark.infrastructure.log.Logger;
import com.ik.central.aliyunsecurityupdater.entity.ECSSecurityUpdatePojo;
import com.ik.central.aliyunsecurityupdater.entity.bo.ECSSecurityInfoBo;
import com.ik.central.aliyunsecurityupdater.exception.LogicException;
import com.ik.central.aliyunsecurityupdater.utils.AliYunSDKUtil;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateECSSecurityService {
  private static final Logger LOGGER = LogFactory.getLogger(UpdateECSSecurityService.class);

  @SuppressWarnings("java:S135")
  public void update(ECSSecurityUpdatePojo[] ecs, String ip) {
    if (ecs == null) {
      return;
    }
    for (ECSSecurityUpdatePojo e : ecs) {
      LOGGER.inf("开始更新ECS安全组[%s -> %s]!", e.getSecurityGroupId(), e.getDescription());
      long startTime = System.currentTimeMillis();
      long durationTime;
      // 创建Client
      Client client = AliYunSDKUtil.getECSClient(e.getEndpoint());
      ECSSecurityInfoBo infoBo =
        getSecurityInfo(client, e.getRegionId(), e.getSecurityGroupId(), e.getDescription());
      // 如果没获取到安全组信息，则不更新
      if (infoBo == null) {
        LOGGER.err("无法找到需要更新的ECS安全组/规则信息!");
        continue;
      }
      // 如果当前IP与安全组放行IP一致，则不更新
      if (Objects.equals(infoBo.getCurrentIp(), ip)) {
        durationTime = System.currentTimeMillis() - startTime;
        LOGGER.inf(
          "无需更新ECS安全组[%s -> %s]! 用时[%sms]",
          e.getSecurityGroupId(), e.getDescription(), durationTime
        );
        continue;
      }
      // 更新安全组放行IP
      LOGGER.inf(
        "开始更新ECS安全组[%s][%s -> %s]...",
        infoBo.getSecurityGroupName(), infoBo.getCurrentIp(), ip
      );
      boolean rs = updateSecurityIP(
        client, e.getRegionId(), e.getSecurityGroupId(), infoBo.getSecurityGroupRuleId(), ip
      );
      if (!rs) {
        continue;
      }
      durationTime = System.currentTimeMillis() - startTime;
      LOGGER.key(
        "完成ECS安全组[%s]更新! 用时[%sms]", infoBo.getSecurityGroupName(), durationTime
      );
    }
  }

  public static ECSSecurityInfoBo getSecurityInfo(
    Client client, String regionId, String securityGroupRuleId, String description
  ) {
    try {
      DescribeSecurityGroupAttributeRequest request =
        new DescribeSecurityGroupAttributeRequest()
          .setRegionId(regionId)
          .setNicType("internet")
          .setDirection("all")
          .setSecurityGroupId(securityGroupRuleId);
      DescribeSecurityGroupAttributeResponse response =
        client.describeSecurityGroupAttribute(request);
      if (response.statusCode != 200) {
        throw new LogicException("获取ECS安全组信息失败! 返回代码:%s".formatted(response.statusCode));
      }
      // 根据描述获取安全组规则信息
      ECSSecurityInfoBo infoBo = null;
      for (
        DescribeSecurityGroupAttributeResponseBody
          .DescribeSecurityGroupAttributeResponseBodyPermissionsPermission permission
        : response.getBody().getPermissions().permission
      ) {
        if (Objects.equals(permission.getDescription(), description)) {
          infoBo = new ECSSecurityInfoBo()
            .setSecurityGroupName(response.getBody().securityGroupName)
            .setSecurityGroupRuleId(permission.getSecurityGroupRuleId())
            .setCurrentIp(permission.getSourceCidrIp());
          break;
        }
      }
      if (infoBo == null) {
        throw new LogicException(
          "找不到描述为 %s 的规则! 返回代码:%s".formatted(description, response.statusCode)
        );
      }
      return infoBo;
    } catch (Exception ex) {
      LOGGER.err(ex, "获取ECS安全组信息时出错! 错误信息: %s", ex.getMessage());
    }
    return null;
  }

  public static boolean updateSecurityIP(
    Client client, String regionId, String securityGroupId, String securityGroupRuleId, String newIp
  ) {
    ModifySecurityGroupRuleRequest request = new ModifySecurityGroupRuleRequest()
      .setRegionId(regionId)
      .setSecurityGroupId(securityGroupId)
      .setSecurityGroupRuleId(securityGroupRuleId)
      .setSourceCidrIp(newIp);
    try {
      ModifySecurityGroupRuleResponse response = client.modifySecurityGroupRule(request);
      if (response.statusCode != 200) {
        throw new LogicException("更新ECS安全组信息失败! 返回代码:%s".formatted(response.statusCode));
      }
      return true;
    } catch (Exception ex) {
      LOGGER.err(ex, "更新ECS安全组信息时出错! 错误信息: %s", ex.getMessage());
    }
    return false;
  }

}
