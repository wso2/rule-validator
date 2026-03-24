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

package org.wso2.rule.validator.validator;

/**
 * Options used while validating documents and rulesets.
 */
public class ValidationOptions {

    private Integer yamlCodePointLimit;

    public ValidationOptions() {
    }

    public static ValidationOptions defaults() {
        return new ValidationOptions();
    }

    public Integer getYamlCodePointLimit() {
        return yamlCodePointLimit;
    }

    public void setYamlCodePointLimit(Integer yamlCodePointLimit) {
        this.yamlCodePointLimit = yamlCodePointLimit;
    }
}
