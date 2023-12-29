package com.ik.central.aliyunsecurityupdater.service;

import com.aliyun.rds20140815.Client;
import com.aliyun.rds20140815.models.*;
import com.heavenark.infrastructure.log.LogFactory;
import com.heavenark.infrastructure.log.Logger;
import com.ik.central.aliyunsecurityupdater.entity.RDSSecurityUpdatePojo;
import com.ik.central.aliyunsecurityupdater.exception.LogicException;
import com.ik.central.aliyunsecurityupdater.utils.AliYunSDKUtil;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateRDSSecurityService {
  private static final Logger LOGGER = LogFactory.getLogger(UpdateRDSSecurityService.class);

  @SuppressWarnings("java:S135")
  public void update(RDSSecurityUpdatePojo[] rds, String ip) {
    if (rds == null) {
      return;
    }
    for (RDSSecurityUpdatePojo r : rds) {
      LOGGER.inf("开始更新RDS白名单[%s -> %s]!", r.getDbInstanceId(), r.getIpArrayName());
      long startTime = System.currentTimeMillis();
      long durationTime;
      // 创建Client
      Client client = AliYunSDKUtil.getRDSClient(r.getEndpoint());
      String currentIp = getSecurityInfo(client, r.getDbInstanceId(), r.getIpArrayName());
      // 如果没获取到IP信息，则不更新
      if (currentIp == null) {
        LOGGER.err("无法找到需要更新的RDS/白名单信息!");
        continue;
      }
      // 如果当前IP与安全组放行IP一致，则不更新
      if (Objects.equals(currentIp, ip)) {
        durationTime = System.currentTimeMillis() - startTime;
        LOGGER.inf(
          "无需更新RDS白名单[%s -> %s]! 用时[%sms]",
          r.getDbInstanceId(), r.getIpArrayName(), durationTime
        );
        continue;
      }
      // 更新安全组放行IP
      LOGGER.inf("开始更新RDS白名单[%s][%s -> %s]...", r.getIpArrayName(), currentIp, ip);
      boolean rs = updateSecurityIP(client, r.getDbInstanceId(), r.getIpArrayName(), ip);
      if (!rs) {
        continue;
      }
      durationTime = System.currentTimeMillis() - startTime;
      LOGGER.key(
        "完成RDS白名单[%s -> %s]更新! 用时[%sms]",
        r.getDbInstanceId(), r.getIpArrayName(), durationTime
      );
    }
  }

  public String getSecurityInfo(Client client, String dbInstanceId, String ipArrayName) {
    try {
      DescribeDBInstanceIPArrayListRequest request =
        new DescribeDBInstanceIPArrayListRequest().setDBInstanceId(dbInstanceId);
      DescribeDBInstanceIPArrayListResponse response = client.describeDBInstanceIPArrayList(request);
      if (response.statusCode != 200) {
        throw new LogicException("获取RDS安全组信息失败! 返回代码:%s".formatted(response.statusCode));
      }
      // 根据描述获取RDS白名单组信息
      for (
        DescribeDBInstanceIPArrayListResponseBody
          .DescribeDBInstanceIPArrayListResponseBodyItemsDBInstanceIPArray ipArray
        : response.getBody().getItems().getDBInstanceIPArray()
      ) {
        if (Objects.equals(ipArray.getDBInstanceIPArrayName(), ipArrayName)) {
          return ipArray.getSecurityIPList();
        }
      }
    } catch (Exception ex) {
      LOGGER.err(ex, "获取RDS安全组信息时出错! 错误信息: %s", ex.getMessage());
    }
    return null;
  }

  public boolean updateSecurityIP(Client client, String dbInstanceId, String ipArrayName, String newIp) {
    try {
      ModifySecurityIpsRequest modifySecurityIpsRequest =
        new ModifySecurityIpsRequest()
          .setDBInstanceId(dbInstanceId)
          .setDBInstanceIPArrayName(ipArrayName)
          .setSecurityIps(newIp);
      ModifySecurityIpsResponse response = client.modifySecurityIps(modifySecurityIpsRequest);
      if (response.statusCode != 200) {
        throw new LogicException("更新RDS白名单信息失败! 返回代码:%s".formatted(response.statusCode));
      }
      return true;
    } catch (Exception ex) {
      LOGGER.err(ex, "更新RDS白名单信息时出错! 错误信息: %s", ex.getMessage());
    }
    return false;
  }

}
