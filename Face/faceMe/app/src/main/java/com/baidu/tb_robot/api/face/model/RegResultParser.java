/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.tb_robot.api.face.model;

import android.util.Log;

import com.baidu.tb_robot.api.face.exception.FaceError;
import com.baidu.tb_robot.api.face.utils.Parser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

public class RegResultParser implements Parser<RegResult> {


    @Override
    public RegResult parse(String json) throws FaceError {
        Log.e("xx", "oarse:" + json);
        try {
            JSONObject jsonObject = new JSONObject(json);

            if (jsonObject.has("error_code")) {
                FaceError error = new FaceError(jsonObject.optInt("error_code"), jsonObject.optString("error_msg"));
                throw error;
            }

            Gson gson = new Gson();
            Type type = new TypeToken<RegResult>() {}.getType();
            return gson.fromJson(json,type);

        } catch (JSONException e) {
            e.printStackTrace();
            FaceError error = new FaceError(FaceError.ErrorCode.JSON_PARSE_ERROR, "Json parse error:" + json, e);
            throw error;
        }
    }
}
