/*
 *  Copyright (c) 2026, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.json.path.evaluator.document.wrappers;

/**
 * Wraps boolean values so they can be tracked by identity in traversal maps.
 */
public class BooleanWrapper {

    public Boolean value;

    /**
     * Creates a wrapper for a boolean value.
     *
     * @param value boolean value to wrap
     */
    public BooleanWrapper(Boolean value) {
        this.value = value;
    }

    /**
     * Returns the wrapped value as a string.
     *
     * @return string representation of the wrapped value
     */
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
