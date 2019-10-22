package com.zju.dcss;

/**
 * 供客户端操作
 */
public interface Cache {
    void put(String key, Object value);

    void put(String host, String key, Object value);

    Object get(String key);

    Object get(String host, String key);

    Object remove(String key);

    Object remove(String host, String key);

}
