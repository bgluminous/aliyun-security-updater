package com.ik.central.aliyunsecurityupdater.service;

import com.aliyun.alidns20150109.Client;
import com.aliyun.alidns20150109.models.*;
import com.heavenark.infrastructure.log.LogFactory;
import com.heavenark.infrastructure.log.Logger;
import com.ik.central.aliyunsecurityupdater.entity.DomainDNSUpdatePojo;
import com.ik.central.aliyunsecurityupdater.entity.bo.DomainDNSInfoBo;
import com.ik.central.aliyunsecurityupdater.exception.LogicException;
import com.ik.central.aliyunsecurityupdater.utils.AliYunSDKUtil;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateDomainDNSService {
  private static final Logger LOGGER = LogFactory.getLogger(UpdateDomainDNSService.class);

  @SuppressWarnings("java:S135")
  public void update(DomainDNSUpdatePojo[] dns, String ip) {
    if (dns == null) {
      return;
    }
    for (DomainDNSUpdatePojo d : dns) {
      LOGGER.inf("开始更新DNS记录[%s -> %s]!", d.getDomainName(), d.getRr());
      long startTime = System.currentTimeMillis();
      long durationTime;
      // 创建Client
      Client client = AliYunSDKUtil.getDNSClient(d.getEndpoint());
      DomainDNSInfoBo infoBo = getDNSInfo(client, d.getDomainName(), d.getRr());
      // 如果没获取到DNS信息，则不更新
      if (infoBo == null) {
        LOGGER.err("无法找到需要更新的DNS记录!");
        continue;
      }
      // 如果当前IP与DNS记录IP一致，则不更新
      if (Objects.equals(infoBo.getCurrentIp(), ip)) {
        durationTime = System.currentTimeMillis() - startTime;
        LOGGER.inf(
          "无需更新DNS记录[%s -> %s]! 用时[%sms]", d.getDomainName(), d.getRr(), durationTime
        );
        continue;
      }
      // 更新安全组放行IP
      LOGGER.inf(
        "开始更新DNS记录[%s -> %s][%s -> %s]...",
        d.getDomainName(), d.getRr(), infoBo.getCurrentIp(), ip
      );
      boolean rs = updateDNS(client, infoBo.getRecordId(), d.getRr(), ip);
      if (!rs) {
        continue;
      }
      durationTime = System.currentTimeMillis() - startTime;
      LOGGER.key(
        "完成DNS记录[%s -> %s]更新! 用时[%sms]", d.getDomainName(), d.getRr(), durationTime
      );
    }
  }

  private DomainDNSInfoBo getDNSInfo(Client client, String domainName, String rr) {
    try {
      DescribeDomainRecordsRequest request =
        new DescribeDomainRecordsRequest().setDomainName(domainName);
      DescribeDomainRecordsResponse response = client.describeDomainRecords(request);
      if (response.statusCode != 200) {
        throw new LogicException("获取DNS信息失败! 返回代码:%s".formatted(response.statusCode));
      }
      // 根据描述获取DNS信息
      for (
        DescribeDomainRecordsResponseBody
          .DescribeDomainRecordsResponseBodyDomainRecordsRecord domainRecord
        : response.getBody().getDomainRecords().getRecord()
      ) {
        if (Objects.equals(domainRecord.getRR(), rr)) {
          return new DomainDNSInfoBo()
            .setRecordId(domainRecord.getRecordId())
            .setCurrentIp(domainRecord.getValue());
        }
      }
    } catch (Exception ex) {
      LOGGER.err(ex, "获取DNS信息时出错! 错误信息: %s", ex.getMessage());
    }
    return null;
  }

  public boolean updateDNS(Client client, String recordId, String rr, String ip) {
    try {
      UpdateDomainRecordRequest request = new UpdateDomainRecordRequest()
        .setRecordId(recordId)
        .setType("A")
        .setRR(rr)
        .setValue(ip);
      UpdateDomainRecordResponse response = client.updateDomainRecord(request);
      if (response.statusCode != 200) {
        throw new LogicException("更新DNS信息失败! 返回代码:%s".formatted(response.statusCode));
      }
      return true;
    } catch (Exception ex) {
      LOGGER.err(ex, "更新DNS信息时出错! 错误信息: %s", ex.getMessage());
    }
    return false;
  }

}
