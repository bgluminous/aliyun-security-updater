package com.ik.central.aliyunsecurityupdater.service;

import com.heavenark.infrastructure.log.LogFactory;
import com.heavenark.infrastructure.log.Logger;
import com.ik.central.aliyunsecurityupdater.ConfigInstance;
import com.ik.central.aliyunsecurityupdater.SingletonStorage;
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
  private static final Logger LOGGER = LogFactory.getLogger(ScheduledService.class);

  @Resource
  private UpdateECSSecurityService updateECSSecurityService;
  @Resource
  private UpdateRDSSecurityService updateRDSSecurityService;
  @Resource
  private UpdateDomainDNSService updateDomainDNSService;

  private static final CustomConfigPojo config = ConfigInstance.getConfig();

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.addCronTask(this::update, config.getCron());
  }

  private void update() {
    String ip = IpApiUtil.getIp();
    // 如果没获取到IP，则不更新 (错误信息已经在上层抛出)
    if (Objects.equals(ip, "null")) {
      return;
    }
    // 比较缓存(节省查询API额度)
    String cacheIp = (String) SingletonStorage.getData("ip");
    if (Objects.equals(cacheIp, ip)) {
      LOGGER.inf("IP与缓存比较无变化！跳过任务(～￣▽￣)～");
      return;
    }

    updateDomainDNSService.update(config.getDns(), ip);
    updateECSSecurityService.update(config.getEcs(), ip);
    updateRDSSecurityService.update(config.getRds(), ip);

    SingletonStorage.updateData("ip", ip);
  }
}
