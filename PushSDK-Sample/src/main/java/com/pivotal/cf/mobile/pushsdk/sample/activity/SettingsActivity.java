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

package com.pivotal.cf.mobile.pushsdk.sample.activity;

import com.pivotal.cf.mobile.common.sample.activity.BaseSettingsActivity;
import com.pivotal.cf.mobile.pushsdk.sample.R;
import com.pivotal.cf.mobile.pushsdk.sample.util.Settings;

public class SettingsActivity extends BaseSettingsActivity {

    protected String[] getPrefererenceNames() {
        return Settings.PREFERENCE_NAMES;
    }

    protected int getPreferencesXmlResourceId() {
        return R.xml.preferences;
    }
}
