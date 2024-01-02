package com.ik.central.aliyunsecurityupdater.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heavenark.infrastructure.log.LogFactory;
import com.heavenark.infrastructure.log.Logger;
import com.ik.central.aliyunsecurityupdater.ConfigInstance;
import com.ik.central.aliyunsecurityupdater.entity.CustomConfigPojo;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * 获取IP工具类
 */
public class IpApiUtil {
  private static final Logger LOGGER = LogFactory.getLogger(IpApiUtil.class);

  private static final CustomConfigPojo config = ConfigInstance.getConfig();

  private IpApiUtil() {
  }

  /**
   * 通过Http请求获取IP
   *
   * @return IP
   */
  public static String getIp() {
    String api = config.getIpApi() == null || config.getIpApi().isEmpty()
      ? "https://api.kiiiv.com/v1/openapi/other/ip"
      : config.getIpApi();
    String resStr = "null";
    try {
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request =
        HttpRequest.newBuilder(URI.create(api)).build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      resStr = response.body();
      ObjectMapper objectMapper = new ObjectMapper();
      Map<String, String> node = objectMapper.readValue(resStr, new TypeReference<>() {
      });
      resStr = node.get("data");
    } catch (JsonParseException ex) {
      LOGGER.err(ex, "解析Json错误（；´д｀）ゞ错误信息:%s 原始数据：%s", ex.getMessage(), resStr);
    } catch (IOException | InterruptedException ex) {
      LOGGER.err(ex, "从OpenAPI获取IP错误（；´д｀）ゞ错误信息:%s", ex.getMessage());
      Thread.currentThread().interrupt();
    }
    return resStr;
  }


}
