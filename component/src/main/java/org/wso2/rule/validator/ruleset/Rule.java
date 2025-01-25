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
package org.wso2.rule.validator.ruleset;

import org.apache.commons.lang3.StringUtils;
import org.wso2.rule.validator.DiagnosticSeverity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rule class to represent a rule in a ruleset.
 */
public class Rule {
    public String name;
    private String description;
    public String message;
    private DiagnosticSeverity severity;
    private boolean resolved;
    public List<RuleThen> then;
    public List<String> given;
    private List<Format> formats;
    private boolean enabled;

    public Rule(String name, Map<String, Object> ruleData, HashMap<String, RulesetAliasDefinition> aliases) {
        this.name = name;
        Object descriptionObject = ruleData.get("description");
        Object messageObject = ruleData.get("message");
        Object severityObject = ruleData.get("severity");
        Object resolvedObject = ruleData.get("resolved");
        Object thenObject = ruleData.get("then");
        Object givenObject = ruleData.get("given");
        Object formatsObject = ruleData.get("formats");

        // TODO: Read enabled
        this.enabled = true;

        if (descriptionObject instanceof String) {
            this.description = (String) descriptionObject;
        } else {
            this.description = "";
        }

        if (messageObject instanceof String) {
            this.message = (String) messageObject;
        } else {
            this.message = "";
        }

        // TODO: Implement message placeholder logic

        if (severityObject instanceof String) {
            this.severity = DiagnosticSeverity.valueOf(StringUtils.toRootUpperCase((String) severityObject));
        } else {
            this.severity = DiagnosticSeverity.ERROR;
        }

        if (resolvedObject instanceof Boolean) {
            this.resolved = (Boolean) resolvedObject;
        } else {
            this.resolved = false;
        }

        // TODO: Implement Recommended

        this.formats = new ArrayList<>();
        if (formatsObject instanceof List) {
            this.formats = Format.getFormatListFromObject((List<String>) formatsObject);
        }

        if (thenObject instanceof List) {
            this.then = new ArrayList<>();
            for (Object thenItem : (List<Object>) thenObject) {
                this.then.add(new RuleThen((Map<String, Object>) thenItem));
            }
        } else if (thenObject instanceof Map) {
            this.then = new ArrayList<>();
            this.then.add(new RuleThen((Map<String, Object>) thenObject));
        }

        if (givenObject instanceof List) {
            this.given = (List<String>) givenObject;
        } else if (givenObject instanceof String) {
            this.given = new ArrayList<>();
            this.given.add((String) givenObject);
        }

        // resolve given aliases
        ArrayList<String> resolvedGiven = new ArrayList<>();
        for (String given : this.given) {
            if (given.startsWith("#")) {
                resolvedGiven.addAll(RulesetAliasDefinition.resolveAliasGiven(given, aliases));
            } else {
                resolvedGiven.add(given);
            }
        }
        this.given = resolvedGiven;
    }

}
