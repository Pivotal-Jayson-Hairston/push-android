/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.push.backend;

import android.test.AndroidTestCase;

import java.io.IOException;

import io.pivotal.android.push.RegistrationParameters;
import io.pivotal.android.push.util.DelayedLoop;
import io.pivotal.android.push.util.FakeHttpURLConnection;
import io.pivotal.android.push.util.FakeNetworkWrapper;

public class PCFPushUnregisterDeviceApiRequestImplTest extends AndroidTestCase {

    private static final long TEN_SECOND_TIMEOUT = 10000L;
    private static final String TEST_PCF_PUSH_DEVICE_REGISTRATION_ID = "TEST_PCF_PUSH_DEVICE_REGISTRATION_ID";
    private static final String TEST_GCM_SENDER_ID = "TEST_GCM_SENDER_ID";
    private static final String TEST_PLATFORM_UUID = "TEST_PLATFORM_UUID";
    private static final String TEST_PLATFORM_SECRET = "TEST_PLATFORM_SECRET";
    private static final String TEST_DEVICE_ALIAS = "TEST_DEVICE_ALIAS";
    private static final String TEST_SERVICE_URL = "http://test.com";

    private RegistrationParameters parameters;
    private FakeNetworkWrapper networkWrapper;
    private DelayedLoop delayedLoop;
    private PCFPushUnregisterDeviceListener PCFPushUnregisterDeviceListener;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        parameters = new RegistrationParameters(TEST_GCM_SENDER_ID, TEST_PLATFORM_UUID, TEST_PLATFORM_SECRET, TEST_SERVICE_URL, TEST_DEVICE_ALIAS, null, false);
        networkWrapper = new FakeNetworkWrapper();
        delayedLoop = new DelayedLoop(TEN_SECOND_TIMEOUT);
        FakeHttpURLConnection.reset();
    }

    public void testRequiresNetworkWrapper() {
        try {
            new PCFPushUnregisterDeviceApiRequestImpl(null);
            fail("Should not have succeeded");
        } catch (IllegalArgumentException ex) {
            // Success
        }
    }

    public void testRequiresPCFPushDeviceRegistrationId() {
        try {
            final PCFPushUnregisterDeviceApiRequestImpl request = new PCFPushUnregisterDeviceApiRequestImpl(new FakeNetworkWrapper());
            makePCFPushUnegisterDeviceApiRequestListener(true, false);
            request.startUnregisterDevice(null, parameters, PCFPushUnregisterDeviceListener);
            fail("Should not have succeeded");
        } catch (IllegalArgumentException ex) {
            // Success
        }
    }

    public void testRequiresParameters() {
        try {
            final PCFPushUnregisterDeviceApiRequestImpl request = new PCFPushUnregisterDeviceApiRequestImpl(new FakeNetworkWrapper());
            makePCFPushUnegisterDeviceApiRequestListener(true, false);
            request.startUnregisterDevice(TEST_PCF_PUSH_DEVICE_REGISTRATION_ID, null, PCFPushUnregisterDeviceListener);
            fail("Should not have succeeded");
        } catch (IllegalArgumentException ex) {
            // Success
        }
    }

    public void testRequiresListener() {
        try {
            final PCFPushUnregisterDeviceApiRequestImpl request = new PCFPushUnregisterDeviceApiRequestImpl(new FakeNetworkWrapper());
            request.startUnregisterDevice(TEST_PCF_PUSH_DEVICE_REGISTRATION_ID, parameters, null);
            fail("Should not have succeeded");
        } catch (IllegalArgumentException ex) {
            // Success
        }
    }

    public void testSuccessfulRequest() {
        makeListenersForSuccessfulRequestFromNetwork(true, 200);
        final PCFPushUnregisterDeviceApiRequestImpl request = new PCFPushUnregisterDeviceApiRequestImpl(networkWrapper);
        request.startUnregisterDevice(TEST_PCF_PUSH_DEVICE_REGISTRATION_ID, parameters, PCFPushUnregisterDeviceListener);
        delayedLoop.startLoop();
        assertTrue(delayedLoop.isSuccess());
    }

    public void testSuccessfulRequestSsl() {
        parameters = new RegistrationParameters(TEST_GCM_SENDER_ID, TEST_PLATFORM_UUID, TEST_PLATFORM_SECRET, TEST_SERVICE_URL, TEST_DEVICE_ALIAS, null, true);
        makeListenersForSuccessfulRequestFromNetworkSsl(true, 200);
        final PCFPushUnregisterDeviceApiRequestImpl request = new PCFPushUnregisterDeviceApiRequestImpl(networkWrapper);
        request.startUnregisterDevice(TEST_PCF_PUSH_DEVICE_REGISTRATION_ID, parameters, PCFPushUnregisterDeviceListener);
        delayedLoop.startLoop();
        assertTrue(delayedLoop.isSuccess());
    }

    public void testFailed405() {
        makeListenersForSuccessfulRequestFromNetwork(false, 405);
        final PCFPushUnregisterDeviceApiRequestImpl request = new PCFPushUnregisterDeviceApiRequestImpl(networkWrapper);
        request.startUnregisterDevice(TEST_PCF_PUSH_DEVICE_REGISTRATION_ID, parameters, PCFPushUnregisterDeviceListener);
        delayedLoop.startLoop();
        assertTrue(delayedLoop.isSuccess());
    }

    // 404 errors are not considered failures
    public void testSuccessful404() {
        makeListenersForSuccessfulRequestFromNetwork(true, 404);
        final PCFPushUnregisterDeviceApiRequestImpl request = new PCFPushUnregisterDeviceApiRequestImpl(networkWrapper);
        request.startUnregisterDevice(TEST_PCF_PUSH_DEVICE_REGISTRATION_ID, parameters, PCFPushUnregisterDeviceListener);
        delayedLoop.startLoop();
        assertTrue(delayedLoop.isSuccess());
    }

    public void testFailed403() {
        makeListenersForSuccessfulRequestFromNetwork(false, 403);
        final PCFPushUnregisterDeviceApiRequestImpl request = new PCFPushUnregisterDeviceApiRequestImpl(networkWrapper);
        request.startUnregisterDevice(TEST_PCF_PUSH_DEVICE_REGISTRATION_ID, parameters, PCFPushUnregisterDeviceListener);
        delayedLoop.startLoop();
        assertTrue(delayedLoop.isSuccess());
    }

    public void testCouldNotConnect() {
        makeListenersFromFailedRequestFromNetwork("Your server is busted");
        final PCFPushUnregisterDeviceApiRequestImpl request = new PCFPushUnregisterDeviceApiRequestImpl(networkWrapper);
        request.startUnregisterDevice(TEST_PCF_PUSH_DEVICE_REGISTRATION_ID, parameters, PCFPushUnregisterDeviceListener);
        delayedLoop.startLoop();
        assertTrue(delayedLoop.isSuccess());
    }

    private void makeListenersForSuccessfulRequestFromNetwork(boolean isSuccessfulResult, int expectedHttpStatusCode) {
        FakeHttpURLConnection.setResponseCode(expectedHttpStatusCode);
        makePCFPushUnegisterDeviceApiRequestListener(isSuccessfulResult, false);
    }

    private void makeListenersForSuccessfulRequestFromNetworkSsl(boolean isSuccessfulResult, int expectedHttpStatusCode) {
        FakeHttpURLConnection.setResponseCode(expectedHttpStatusCode);
        makePCFPushUnegisterDeviceApiRequestListener(isSuccessfulResult, true);
    }

    private void makeListenersFromFailedRequestFromNetwork(String exceptionText) {
        IOException exception = null;
        if (exceptionText != null) {
            exception = new IOException(exceptionText);
        }
        FakeHttpURLConnection.willThrowConnectionException(true);
        FakeHttpURLConnection.setConnectionException(exception);
        makePCFPushUnegisterDeviceApiRequestListener(false, false);
    }

    public void makePCFPushUnegisterDeviceApiRequestListener(final boolean isSuccessfulRequest, final boolean isTrustAllSslCertificates) {
        PCFPushUnregisterDeviceListener = new PCFPushUnregisterDeviceListener() {

            @Override
            public void onPCFPushUnregisterDeviceSuccess() {
                assertTrue(isSuccessfulRequest);
                assertEquals("DELETE", FakeHttpURLConnection.getReceivedHttpMethod());
                assertTrue(FakeHttpURLConnection.getRequestPropertiesMap().containsKey("Authorization"));
                assertEquals(isTrustAllSslCertificates, FakeHttpURLConnection.didCallSetSSLSocketFactory());

                if (isSuccessfulRequest) {
                    assertTrue(FakeHttpURLConnection.getReceivedURL().toString().endsWith(TEST_PCF_PUSH_DEVICE_REGISTRATION_ID));
                    delayedLoop.flagSuccess();
                } else {
                    delayedLoop.flagFailure();
                }
            }

            @Override
            public void onPCFPushUnregisterDeviceFailed(String reason) {
                assertFalse(isSuccessfulRequest);
                if (isSuccessfulRequest) {
                    delayedLoop.flagFailure();
                } else {
                    delayedLoop.flagSuccess();
                }
            }
        };
    }
}