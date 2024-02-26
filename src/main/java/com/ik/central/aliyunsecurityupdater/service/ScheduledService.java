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

import java.io.IOException;
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

  @SuppressWarnings("java:S3776")
  private void update() {
    int times = 0;
    int retryTimes = config.getRetryTimes();
    String ip = null;
    // 尝试多次获取IP
    while (times <= retryTimes && ip == null) {
      if (times != 0) {
        LOGGER.war("第 %s 次尝试重新获取IP...".formatted(times));
      }
      try {
        ip = IpApiUtil.getIp();
      } catch (IOException ex) {
        LOGGER.err("从OpenAPI获取IP错误（；´д｀）ゞ错误信息:%s", ex.getMessage());
      } catch (InterruptedException ex) {
        LOGGER.err("发送请求错误（；´д｀）ゞ错误信息:%s", ex.getMessage());
        Thread.currentThread().interrupt();
      } finally {
        times++;
      }
      // 如果没有获取到IP才需要延迟执行
      if (ip == null && times < retryTimes) {
        LOGGER.war("获取IP地址失败! %s秒后重试...", config.getRetryDelay());
        try {
          Thread.sleep(config.getRetryDelay() * 1000L);
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
        }
      }
    }
    // 如果没获取到IP，则不更新 (错误信息已经在上层抛出)
    if (ip == null) {
      LOGGER.err("无法获取IP地址, 请检查日志获取更多信息!");
      return;
    }
    // 比较缓存(节省查询API额度)
    String cacheIp = (String) SingletonStorage.getData("ip");
    if (Objects.equals(cacheIp, ip)) {
      LOGGER.inf("IP与缓存比较无变化！跳过任务(～￣▽￣)～");
      return;
    }
    // 更新数据
    updateDomainDNSService.update(config.getDns(), ip);
    updateECSSecurityService.update(config.getEcs(), ip);
    updateRDSSecurityService.update(config.getRds(), ip);

    SingletonStorage.updateData("ip", ip);
  }
}
