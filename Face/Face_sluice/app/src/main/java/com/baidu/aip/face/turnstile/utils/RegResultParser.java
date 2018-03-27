/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.aip.face.turnstile.utils;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.aip.face.turnstile.exception.FaceError;
import com.baidu.aip.face.turnstile.model.RegResult;

public class RegResultParser implements Parser<RegResult> {

    @Override
    public RegResult parse(String json) throws FaceError {
        try {
            JSONObject jsonObject = new JSONObject(json);

            if (jsonObject.has("error_code")) {
                throw new FaceError(jsonObject.optInt("error_code"), jsonObject.optString("error_msg"));
            }

            RegResult result = new RegResult();
            result.setLogId(jsonObject.optLong("log_id"));
            result.setJsonRes(json);
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            throw new FaceError(FaceError.ErrorCode.JSON_PARSE_ERROR, "Json parse error:" + json, e);
        }
    }
}
