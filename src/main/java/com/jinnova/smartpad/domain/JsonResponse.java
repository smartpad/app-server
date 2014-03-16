// Copyright (c) 2012 Health Market Science, Inc.
package com.jinnova.smartpad.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

public class JsonResponse extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public static final String DATA_FIELD = "data";
    public static final String SUCCESS_FIELD = "success";
    public static final String ERROR_FIELD = "error";
    public static final String REASON_FIELD = "reason";
    
    public JsonResponse() {
    }

    public JsonResponse(Map<String, ?> data) {
        put(SUCCESS_FIELD, !MapUtils.isEmpty(data));
        if (data != null) {
            putAll(data);
        }
    }
    
    public JsonResponse(boolean success, Object data) {
        if (data != null && !(data instanceof Collection)) {
            data = Arrays.asList(data);
        }
        if (data != null) {
            put(DATA_FIELD, data);
        }
        put(SUCCESS_FIELD, success);
    }

    public JsonResponse(boolean success, Object data, String failMessage) {
        if (data != null && !(data instanceof Collection)) {
            data = Arrays.asList(data);
        }
        if (data != null) {
            put(DATA_FIELD, data);
        }
        put(SUCCESS_FIELD, success);
        if (!success) {
            put(REASON_FIELD, failMessage);
        }
    }

    public JsonResponse(boolean success, String... keyValuePairs) {
        if (keyValuePairs.length % 2 == 1) {
            throw new IllegalArgumentException("There is not enough pairs of key and value");
        }
        put(SUCCESS_FIELD, success);
        for (int i = 0; i < keyValuePairs.length / 2; i++) {
            put(keyValuePairs[i], keyValuePairs[i + 1]);
        }
    }
    
    public void putError(String error) {
        put(ERROR_FIELD, error);
    }
}