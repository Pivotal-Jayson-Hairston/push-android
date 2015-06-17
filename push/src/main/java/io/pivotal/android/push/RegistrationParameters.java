/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.push;

import java.util.HashSet;
import java.util.Set;

/**
 * Parameters used to register with the Pivotal CF Mobile Services Push server.
 */
public class RegistrationParameters {

    private final String gcmSenderId;
    private final String platformUuid;
    private final String platformSecret;
    private final String serviceUrl;
    private final String deviceAlias;
    private final Set<String> tags;
    private final boolean trustAllSslCertificates;

    /**
     * Sets up parameters used by the Pivotal CF Mobile Services Push SDK
     * @param gcmSenderId    The "sender ID" or "project ID", as defined by the Google Cloud Messaging.  May not be null or empty.
     *                       You can find it on the Google Cloud Console (https://cloud.google.com) for your project.
     * @param platformUuid   The "platform", as defined by Pivotal CF Mobile Services Push Services for your platform.  May not be null or empty.
     * @param platformSecret The "platform secret", as defined by Pivotal CF Mobile Services Push Services for your platform.  May not be null or empty.
     * @param serviceUrl     The Pivotal CF Mobile Services server used to provide push and related analytics services.
     * @param deviceAlias    A developer-defined "device alias" which can be used to designate this device, or class.
*                       of devices, in push or notification campaigns. May not be set to `null`. May be set to empty.
     * @param tags           A set of tags to register to.  You should always register all tags that you want to listen to, even if you have
*                       already subscribed to them.  If you exclude any subscribed tags in a registration request, then those tags
     * @param trustAllSslCertificates  'true' if all SSL certificates should be trusted. You should use 'false' unless otherwise required.
     */
    public RegistrationParameters(String gcmSenderId, String platformUuid, String platformSecret, String serviceUrl, String deviceAlias, Set<String> tags, boolean trustAllSslCertificates) {
        this.gcmSenderId = gcmSenderId;
        this.platformUuid = platformUuid;
        this.platformSecret = platformSecret;
        this.serviceUrl = serviceUrl;
        this.deviceAlias = deviceAlias;
        this.tags = tags;
        this.trustAllSslCertificates = trustAllSslCertificates;
    }

    public String getGcmSenderId() {
        return gcmSenderId;
    }

    public String getPlatformUuid() {
        return platformUuid;
    }

    public String getPlatformSecret() {
        return platformSecret;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public String getDeviceAlias() {
        return deviceAlias;
    }

    public Set<String> getTags() {
        return tags != null ? tags : new HashSet<String>();
    }

    public boolean isTrustAllSslCertificates() {
        return trustAllSslCertificates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegistrationParameters that = (RegistrationParameters) o;

        if (trustAllSslCertificates != that.trustAllSslCertificates) return false;
        if (gcmSenderId != null ? !gcmSenderId.equals(that.gcmSenderId) : that.gcmSenderId != null)
            return false;
        if (platformUuid != null ? !platformUuid.equals(that.platformUuid) : that.platformUuid != null)
            return false;
        if (platformSecret != null ? !platformSecret.equals(that.platformSecret) : that.platformSecret != null)
            return false;
        if (serviceUrl != null ? !serviceUrl.equals(that.serviceUrl) : that.serviceUrl != null)
            return false;
        if (deviceAlias != null ? !deviceAlias.equals(that.deviceAlias) : that.deviceAlias != null)
            return false;
        return !(tags != null ? !tags.equals(that.tags) : that.tags != null);

    }

    @Override
    public int hashCode() {
        int result = gcmSenderId != null ? gcmSenderId.hashCode() : 0;
        result = 31 * result + (platformUuid != null ? platformUuid.hashCode() : 0);
        result = 31 * result + (platformSecret != null ? platformSecret.hashCode() : 0);
        result = 31 * result + (serviceUrl != null ? serviceUrl.hashCode() : 0);
        result = 31 * result + (deviceAlias != null ? deviceAlias.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = 31 * result + (trustAllSslCertificates ? 1 : 0);
        return result;
    }
}
