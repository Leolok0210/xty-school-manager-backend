package com.xiaotiyun.school.manager.service;

public interface DeviceTokenService {
    String getOrCreateToken(String deviceSn, String deviceName);

    boolean validateToken(String token);

    void revokeToken(String deviceSn);
}
