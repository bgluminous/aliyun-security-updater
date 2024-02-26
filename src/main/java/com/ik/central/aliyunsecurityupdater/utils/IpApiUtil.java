package com.ik.central.aliyunsecurityupdater.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
  private static final CustomConfigPojo config = ConfigInstance.getConfig();

  private IpApiUtil() {
  }

  /**
   * 通过Http请求获取IP
   *
   * @return IP
   */
  public static String getIp() throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    URI uri = URI.create(config.getIpApi());
    HttpRequest request = HttpRequest.newBuilder(uri).build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    String res = response.body();
    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, String> node = objectMapper.readValue(res, new TypeReference<>() {
    });
    return node.get(config.getResponseJsonIpNodeName());
  }

}
