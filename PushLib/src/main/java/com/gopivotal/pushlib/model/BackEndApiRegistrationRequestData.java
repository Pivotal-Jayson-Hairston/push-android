package com.gopivotal.pushlib.model;

/*
{
        "release_uuid": "guid_provided_by_developer",
        "secret": "guid_provided_by_developer",
        "device_alias": "developer-specific",
        "device_type": "phone"|"tablet"|"phablet",
        "device_model": "Nexus 4",
        "os": "android",
        "os_version": "4.4",
        "registration_token": "provided_by_GCM"
        }
*/

import com.google.gson.annotations.SerializedName;

public class BackEndApiRegistrationRequestData {

    @SerializedName("release_uuid")
    private String releaseUuid;

    @SerializedName("secret")
    private String secret;

    @SerializedName("device_alias")
    private String deviceAlias;

    @SerializedName("device_type")
    private String deviceType;

    @SerializedName("device_model")
    private String deviceModel;

    @SerializedName("os")
    private String os;

    @SerializedName("os_version")
    private String osVersion;

    @SerializedName("registration_token")
    private String registrationToken;

    public BackEndApiRegistrationRequestData() {
    }

    public String getReleaseUuid() {
        return releaseUuid;
    }

    public void setReleaseUuid(String releaseUuid) {
        this.releaseUuid = releaseUuid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getDeviceAlias() {
        return deviceAlias;
    }

    public void setDeviceAlias(String deviceAlias) {
        this.deviceAlias = deviceAlias;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getRegistrationToken() {
        return registrationToken;
    }

    public void setRegistrationToken(String registrationToken) {
        this.registrationToken = registrationToken;
    }
}
