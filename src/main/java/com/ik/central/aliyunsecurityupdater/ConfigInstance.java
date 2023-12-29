package com.ik.central.aliyunsecurityupdater;

import com.ik.central.aliyunsecurityupdater.entity.CustomConfigPojo;
import com.ik.central.aliyunsecurityupdater.exception.ConfigInitError;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;

public class ConfigInstance {

  private CustomConfigPojo config;

  private ConfigInstance() {
  }

  public static void init() {
    try (InputStream is = new FileInputStream("/settings.yml")) {
      Holder.INSTANCE.config = new Yaml().loadAs(is, CustomConfigPojo.class);
    } catch (Exception ex) {
      throw new ConfigInitError("读取配置文件时错误! 错误信息: %s".formatted(ex.getMessage()));
    }
  }

  public static CustomConfigPojo getConfig() {
    return Holder.INSTANCE.config;
  }

  private static class Holder {
    private static final ConfigInstance INSTANCE = new ConfigInstance();
  }
}
