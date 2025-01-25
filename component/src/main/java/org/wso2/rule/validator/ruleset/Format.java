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
}
