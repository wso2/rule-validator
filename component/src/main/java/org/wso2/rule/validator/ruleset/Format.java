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
import java.util.List;

/**
 * Enum for the supported document formats
 */
public enum Format {
    ARAZZO1_0,
    AAS2,
    AAS2_0,
    AAS2_1,
    AAS2_2,
    AAS2_3,
    AAS2_4,
    AAS2_5,
    AAS2_6,
    AAS3,
    AAS3_0,
    OAS2,
    OAS3,
    OAS3_0,
    OAS3_1,
    JSON_SCHEMA,
    JSON_SCHEMA_LOOSE,
    JSON_SCHEMA_DRAFT_4,
    JSON_SCHEMA_DRAFT_6,
    JSON_SCHEMA_DRAFT_7,
    JSON_SCHEMA_DRAFT_2019_09,
    JSON_SCHEMA_DRAFT_2020_12;

    public static Format getFormat(String format) {
        switch (format) {
            case "arazzo1_0":
                return Format.ARAZZO1_0;
            case "aas2":
                return Format.AAS2;
            case "aas2_0":
                return Format.AAS2_0;
            case "aas2_1":
                return Format.AAS2_1;
            case "aas2_2":
                return Format.AAS2_2;
            case "aas2_3":
                return Format.AAS2_3;
            case "aas2_4":
                return Format.AAS2_4;
            case "aas2_5":
                return Format.AAS2_5;
            case "aas2_6":
                return Format.AAS2_6;
            case "aas3":
                return Format.AAS3;
            case "aas3_0":
                return Format.AAS3_0;
            case "oas2":
                return Format.OAS2;
            case "oas3":
                return Format.OAS3;
            case "oas3_0":
                return Format.OAS3_0;
            case "oas3_1":
                return Format.OAS3_1;
            case "json_schema":
                return Format.JSON_SCHEMA;
            case "json_schema_loose":
                return Format.JSON_SCHEMA_LOOSE;
            case "json_schema_draft_4":
                return Format.JSON_SCHEMA_DRAFT_4;
            case "json_schema_draft_6":
                return Format.JSON_SCHEMA_DRAFT_6;
            case "json_schema_draft_7":
                return Format.JSON_SCHEMA_DRAFT_7;
            case "json_schema_draft_2019_09":
                return Format.JSON_SCHEMA_DRAFT_2019_09;
            case "json_schema_draft_2020_12":
                return Format.JSON_SCHEMA_DRAFT_2020_12;
            default:
                return null;
        }
    }

    public static ArrayList<Format> getFormatListFromObject(List<String> formatStrings) {
        ArrayList<Format> formats = new ArrayList<>();
        for (String formatString : formatStrings) {
            formats.add(getFormat(formatString));
        }
        return formats;
    }

    public static boolean matchFormat(List<Format> toBeCheckedIn, List<Format> toCheck) {
        if (toCheck == null) {
            return true;
        } else if (toBeCheckedIn == null) {
            return false;
        }
        for (Format format : toCheck) {
            if (toBeCheckedIn.contains(format)) {
                return true;
            }
        }
        return false;
    }
}
