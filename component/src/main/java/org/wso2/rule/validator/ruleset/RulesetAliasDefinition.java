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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to represent a ruleset alias definition.
 * An alias definition can be a simple alias or a complex alias.
 * A simple alias is a list of givens
 * A complex alias is a list of targets and a description
 */
public class RulesetAliasDefinition {
    private final String name;
    private String description;
    public ArrayList<RulesetAliasTarget> targets;
    private boolean isComplexAlias;
    public List<String> given;

    public RulesetAliasDefinition(String name, Object aliasObject) {
        this.name = name;

        if (aliasObject instanceof List) {
            isComplexAlias = false;
            this.given = (List<String>) aliasObject;
        } else if (aliasObject instanceof Map) {
            isComplexAlias = true;
            Map<String, Object> aliasMap = (Map<String, Object>) aliasObject;
            this.description = (String) aliasMap.get("description");
            this.targets = new ArrayList<>();
            ArrayList<Object> targets = (ArrayList<Object>) aliasMap.get("targets");
            for (Object target : targets) {
                RulesetAliasTarget aliasTarget = new RulesetAliasTarget(target);
                this.targets.add(aliasTarget);
            }
        }
    }

    public boolean isComplexAlias() {
        return isComplexAlias;
    }

    public static ArrayList<String> resolveAliasGiven(String given, HashMap<String, RulesetAliasDefinition> aliases) {

        ArrayList<String> resolved = new ArrayList<>();

        String aliasExtractionRegex = "^#[a-zA-Z]+";
        Pattern pattern = Pattern.compile(aliasExtractionRegex);
        Matcher matcher = pattern.matcher(given);
        if (!matcher.find()) {
            resolved.add(given);
            return resolved;
        }
        String aliasName = matcher.group(0);
        RulesetAliasDefinition alias = aliases.get(aliasName.substring(1));

        if (alias.isComplexAlias()) {
            for (RulesetAliasTarget target : alias.targets) {
                // TODO: Match target with document format
                for (String g : target.given) {
                    resolved.add(given.replaceFirst(aliasExtractionRegex, "\\" + g));
                }
            }
        } else {
            for (String g : alias.given) {
                resolved.add(given.replaceFirst(aliasExtractionRegex, "\\" + g));
            }
        }

        return resolved;
    }
}

