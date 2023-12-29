package com.ik.central.aliyunsecurityupdater.service;

import com.ik.central.aliyunsecurityupdater.ConfigInstance;
import com.ik.central.aliyunsecurityupdater.entity.CustomConfigPojo;
import com.ik.central.aliyunsecurityupdater.utils.IpApiUtil;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@EnableScheduling
public class ScheduledService implements SchedulingConfigurer {

  @Resource
  private UpdateECSSecurityService updateECSSecurityService;
  @Resource
  private UpdateRDSSecurityService updateRDSSecurityService;
  @Resource
  private UpdateDomainDNSService updateDomainDNSService;

  private static final CustomConfigPojo config = ConfigInstance.getConfig();

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.addCronTask(() -> {
      String ip = IpApiUtil.getIp();
      // 如果没获取到IP，则不更新 (错误信息已经在上层抛出)
      if (Objects.equals(ip, "null")) {
        return;
      }
      updateDomainDNSService.update(config.getDns(), ip);
      updateECSSecurityService.update(config.getEcs(), ip);
      updateRDSSecurityService.update(config.getRds(), ip);
    }, config.getCron());
  }
}
