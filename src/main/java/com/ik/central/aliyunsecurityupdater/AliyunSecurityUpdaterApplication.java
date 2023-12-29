package com.ik.central.aliyunsecurityupdater;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AliyunSecurityUpdaterApplication {

  public static void main(String[] args) {
    ConfigInstance.init();
    SpringApplication.run(AliyunSecurityUpdaterApplication.class, args);
  }

}
