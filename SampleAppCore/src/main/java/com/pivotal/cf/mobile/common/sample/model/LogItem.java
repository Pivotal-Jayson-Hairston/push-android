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

package com.pivotal.cf.mobile.common.sample.model;

public class LogItem {

    public final String timestamp;
    public final String message;
    public final int baseRowColour;

    public LogItem(String timestamp, String message, int baseRowColour) {
        this.timestamp = timestamp;
        this.message = message;
        this.baseRowColour = baseRowColour;
    }
}