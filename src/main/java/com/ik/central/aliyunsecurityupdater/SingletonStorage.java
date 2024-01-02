package com.ik.central.aliyunsecurityupdater;

import java.util.HashMap;
import java.util.Map;

public class SingletonStorage {

  private final Map<String, Object> dadaMap = new HashMap<>();


  public static Object getData(String key) {
    return Holder.INSTANCE.dadaMap.get(key);
  }

  public static void updateData(String key, Object value) {
    Holder.INSTANCE.dadaMap.put(key, value);
  }

  private static final class Holder {
    private static final SingletonStorage INSTANCE = new SingletonStorage();
  }

}
