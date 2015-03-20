package com.mychatserver.enums;

import org.json.simple.JSONObject;

/**
 * Created by ciprian.mare on 3/20/2015.
 */
public enum Error {
    NO_ERROR(0, "Everything ok"),
    USER_EXIST(1, "User already exist"),
    USER_NOT_EXIST(2, "User not exist"),
    BAD_CREDENTIALS(3, "Bad credentials"),
    COMMON_ERROR(4, "Some problem occurred");

    private final int code;
    private final String description;

    private Error(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return jsonErroMessage(code, description).toJSONString();
    }

    private JSONObject jsonErroMessage (int code, String description) {
        JSONObject json = new JSONObject();
        json.put("errorCode", code);
        json.put("errorDescription", description);
        return json;
    }
}
