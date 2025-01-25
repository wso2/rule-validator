/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.rule.validator.ruleset.file.type;

import com.jayway.jsonpath.JsonPath;
import org.wso2.rule.validator.ruleset.Ruleset;

/**
 * This class is used to parse a JSON ruleset file.
 */
public class JsonRuleset extends Ruleset {
    public JsonRuleset(String rulesetString) {
        super(JsonPath.parse(rulesetString).json());
    }
}
