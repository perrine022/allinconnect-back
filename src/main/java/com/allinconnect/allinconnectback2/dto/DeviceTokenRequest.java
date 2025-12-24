package com.allinconnect.allinconnectback2.dto;

import com.allinconnect.allinconnectback2.model.DeviceEnvironment;
import com.allinconnect.allinconnectback2.model.DevicePlatform;

public class DeviceTokenRequest {
    private String token;
    private DevicePlatform platform;
    private DeviceEnvironment environment;

    public DeviceTokenRequest() {}

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public DevicePlatform getPlatform() { return platform; }
    public void setPlatform(DevicePlatform platform) { this.platform = platform; }
    public DeviceEnvironment getEnvironment() { return environment; }
    public void setEnvironment(DeviceEnvironment environment) { this.environment = environment; }
}
