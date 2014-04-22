/* Copyright (c) 2013 Pivotal Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pivotal.cf.mobile.pushsdk.backend;

import android.test.AndroidTestCase;

import com.pivotal.cf.mobile.pushsdk.RegistrationParameters;
import com.pivotal.cf.mobile.pushsdk.network.FakeHttpURLConnection;
import com.pivotal.cf.mobile.pushsdk.network.FakeNetworkWrapper;
import com.pivotal.cf.mobile.pushsdk.util.DelayedLoop;

import java.io.IOException;

public class BackEndRegistrationApiRequestImplTest extends AndroidTestCase {

    private static final String TEST_BACK_END_DEVICE_REGISTRATION_ID = "TEST_BACK_END_DEVICE_REGISTRATION_ID";
    private static final String TEST_GCM_DEVICE_REGISTRATION_ID = "TEST_GCM_DEVICE_REGISTRATION_ID";
    private static final String TEST_SENDER_ID = "TEST_SENDER_ID";
    private static final String TEST_VARIANT_UUID = "TEST_VARIANT_UUID";
    private static final String TEST_VARIANT_SECRET = "TEST_VARIANT_SECRET";
    private static final String TEST_DEVICE_ALIAS = "TEST_DEVICE_ALIAS";
    private static final long TEN_SECOND_TIMEOUT = 10000L;

    private FakeNetworkWrapper networkWrapper;
    private DelayedLoop delayedLoop;
    private BackEndRegistrationListener backEndRegistrationListener;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        networkWrapper = new FakeNetworkWrapper();
        delayedLoop = new DelayedLoop(TEN_SECOND_TIMEOUT);
        FakeHttpURLConnection.reset();
    }

    public void testRequiresContext() {
        try {
            new BackEndRegistrationApiRequestImpl(null, networkWrapper);
            fail("Should not have succeeded");
        } catch (IllegalArgumentException ex) {
            // Success
        }
    }

    public void testRequiresNetworkWrapper() {
        try {
            new BackEndRegistrationApiRequestImpl(getContext(), null);
            fail("Should not have succeeded");
        } catch (IllegalArgumentException ex) {
            // Success
        }
    }

    public void testRequiresGcmDeviceRegistrationId() {
        try {
            final BackEndRegistrationApiRequestImpl request = new BackEndRegistrationApiRequestImpl(getContext(), new FakeNetworkWrapper());
            makeBackEndRegistrationApiRequestListener(true);
            request.startNewDeviceRegistration(null, getParameters(), backEndRegistrationListener);
            fail("Should not have succeeded");
        } catch (IllegalArgumentException ex) {
            // Success
        }
    }

    public void testRequiresParameters() {
        try {
            final BackEndRegistrationApiRequestImpl request = new BackEndRegistrationApiRequestImpl(getContext(), new FakeNetworkWrapper());
            request.startNewDeviceRegistration(TEST_GCM_DEVICE_REGISTRATION_ID, null, backEndRegistrationListener);
            fail("Should not have succeeded");
        } catch (IllegalArgumentException ex) {
            // Success
        }
    }

    public void testRequiresListener() {
        try {
            final BackEndRegistrationApiRequestImpl request = new BackEndRegistrationApiRequestImpl(getContext(), new FakeNetworkWrapper());
            request.startNewDeviceRegistration(TEST_GCM_DEVICE_REGISTRATION_ID, getParameters(), null);
            fail("Should not have succeeded");
        } catch (IllegalArgumentException ex) {
            // Success
        }
    }

    public void testSuccessfulRequest() {
        makeListenersForSuccessfulRequestFromNetwork(true, 200);
        final BackEndRegistrationApiRequestImpl request = new BackEndRegistrationApiRequestImpl(getContext(), networkWrapper);
        request.startNewDeviceRegistration(TEST_GCM_DEVICE_REGISTRATION_ID, getParameters(), backEndRegistrationListener);
        delayedLoop.startLoop();
        assertTrue(delayedLoop.isSuccess());
    }

    public void testNullResponse() {
        makeListenersForSuccessfulNullResultFromNetwork();
        final BackEndRegistrationApiRequestImpl request = new BackEndRegistrationApiRequestImpl(getContext(), networkWrapper);
        request.startNewDeviceRegistration(TEST_GCM_DEVICE_REGISTRATION_ID, getParameters(), backEndRegistrationListener);
        delayedLoop.startLoop();
        assertTrue(delayedLoop.isSuccess());
    }

    public void testSuccessful404() {
        makeListenersForSuccessfulRequestFromNetwork(false, 404);
        final BackEndRegistrationApiRequestImpl request = new BackEndRegistrationApiRequestImpl(getContext(), networkWrapper);
        request.startNewDeviceRegistration(TEST_GCM_DEVICE_REGISTRATION_ID, getParameters(), backEndRegistrationListener);
        delayedLoop.startLoop();
        assertTrue(delayedLoop.isSuccess());
    }

    public void testCouldNotConnect() {
        makeListenersFromFailedRequestFromNetwork("Your server is busted", 0);
        final BackEndRegistrationApiRequestImpl request = new BackEndRegistrationApiRequestImpl(getContext(), networkWrapper);
        request.startNewDeviceRegistration(TEST_GCM_DEVICE_REGISTRATION_ID, getParameters(), backEndRegistrationListener);
        delayedLoop.startLoop();
        assertTrue(delayedLoop.isSuccess());
    }

    public void testBadNetworkResponse() {
        makeListenersWithBadNetworkResponse();
        final BackEndRegistrationApiRequestImpl request = new BackEndRegistrationApiRequestImpl(getContext(), networkWrapper);
        request.startNewDeviceRegistration(TEST_GCM_DEVICE_REGISTRATION_ID, getParameters(), backEndRegistrationListener);
        delayedLoop.startLoop();
        assertTrue(delayedLoop.isSuccess());
    }

    public void testNoDeviceUuidInResponse() {
        makeListenersWithNoDeviceUuidInResponse();
        final BackEndRegistrationApiRequestImpl request = new BackEndRegistrationApiRequestImpl(getContext(), networkWrapper);
        request.startNewDeviceRegistration(TEST_GCM_DEVICE_REGISTRATION_ID, getParameters(), backEndRegistrationListener);
        delayedLoop.startLoop();
        assertTrue(delayedLoop.isSuccess());
    }

    private void makeListenersForSuccessfulRequestFromNetwork(boolean isSuccessfulResult, int expectedHttpStatusCode) {
        final String resultantJson = "{\"device_uuid\" : \"" + TEST_BACK_END_DEVICE_REGISTRATION_ID + "\"}";
        FakeHttpURLConnection.setResponseData(resultantJson);
        FakeHttpURLConnection.setResponseCode(expectedHttpStatusCode);
        makeBackEndRegistrationApiRequestListener(isSuccessfulResult);
    }

    private void makeListenersForSuccessfulNullResultFromNetwork() {
        FakeHttpURLConnection.setResponseData(null);
        FakeHttpURLConnection.setResponseCode(200);
        makeBackEndRegistrationApiRequestListener(false);
    }

    private void makeListenersWithBadNetworkResponse() {
        FakeHttpURLConnection.setResponseData("{{{{{{{");
        FakeHttpURLConnection.setResponseCode(200);
        makeBackEndRegistrationApiRequestListener(false);
    }

    private void makeListenersWithNoDeviceUuidInResponse() {
        FakeHttpURLConnection.setResponseData("{}");
        FakeHttpURLConnection.setResponseCode(200);
        makeBackEndRegistrationApiRequestListener(false);
    }

    private void makeListenersFromFailedRequestFromNetwork(String exceptionText, int expectedHttpStatusCode) {
        IOException exception = null;
        if (exceptionText != null) {
            exception = new IOException(exceptionText);
        }
        FakeHttpURLConnection.setConnectionException(exception);
        FakeHttpURLConnection.willThrowConnectionException(true);
        FakeHttpURLConnection.setResponseCode(expectedHttpStatusCode);
        makeBackEndRegistrationApiRequestListener(false);
    }

    public void makeBackEndRegistrationApiRequestListener(final boolean isSuccessfulRequest) {
        backEndRegistrationListener = new BackEndRegistrationListener() {

            @Override
            public void onBackEndRegistrationSuccess(String backEndDeviceRegistrationId) {
                assertTrue(isSuccessfulRequest);
                if (isSuccessfulRequest) {
                    delayedLoop.flagSuccess();
                    assertEquals(TEST_BACK_END_DEVICE_REGISTRATION_ID, backEndDeviceRegistrationId);
                } else {
                    delayedLoop.flagFailure();
                }
            }

            @Override
            public void onBackEndRegistrationFailed(String reason) {
                assertFalse(isSuccessfulRequest);
                if (isSuccessfulRequest) {
                    delayedLoop.flagFailure();
                } else {
                    delayedLoop.flagSuccess();
                }
            }
        };
    }

    private RegistrationParameters getParameters() {
        return new RegistrationParameters(TEST_SENDER_ID, TEST_VARIANT_UUID, TEST_VARIANT_SECRET, TEST_DEVICE_ALIAS);
    }
}
