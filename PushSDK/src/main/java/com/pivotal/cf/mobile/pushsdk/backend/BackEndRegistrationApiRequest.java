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

import com.pivotal.cf.mobile.pushsdk.RegistrationParameters;

public interface BackEndRegistrationApiRequest {

    void startNewDeviceRegistration(String gcmDeviceRegistrationId, RegistrationParameters parameters, BackEndRegistrationListener listener);
    void startUpdateDeviceRegistration(String gcmDeviceRegistrationId, String backEndDeviceRegistrationId, RegistrationParameters parameters, BackEndRegistrationListener listener);
    BackEndRegistrationApiRequest copy();

}
