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
import org.wso2.rule.validator.Constants;
import org.wso2.rule.validator.DiagnosticSeverity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Rule class to represent a rule in a ruleset.
 */
public class Rule {
    public String name;
    private String description;
    public String message;
    public DiagnosticSeverity severity;
    private boolean resolved;
    public List<RuleThen> then;
    public List<String> given;
    public List<Format> formats;
    private final List<Format> rulesetFormats;

    public Rule(String name, Map<String, Object> ruleData, Map<String, RulesetAliasDefinition> aliases,
                List<Format> rulesetFormats) {
        this.name = name;
        this.rulesetFormats = new ArrayList<>(rulesetFormats);
        Object descriptionObject = ruleData.get("description");
        Object messageObject = ruleData.get("message");
        Object severityObject = ruleData.get("severity");
        Object resolvedObject = ruleData.get("resolved");
        Object thenObject = ruleData.get("then");
        Object givenObject = ruleData.get("given");
        Object formatsObject = ruleData.get("formats");

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
        List<String> resolvedGiven = new ArrayList<>();
        for (String given : this.given) {
            if (given.startsWith(Constants.ALIAS_PREFIX)) {
                List<Format> aliasFormats;
                if (!this.formats.isEmpty()) {
                    aliasFormats = this.formats;
                } else if (!this.rulesetFormats.isEmpty()) {
                    aliasFormats = this.rulesetFormats;
                } else {
                    aliasFormats = null;
                }
                resolvedGiven.addAll(RulesetAliasDefinition.resolveAliasGiven(given, aliases, aliasFormats));
            } else {
                resolvedGiven.add(given);
            }
        }
        this.given = resolvedGiven;
    }

}
