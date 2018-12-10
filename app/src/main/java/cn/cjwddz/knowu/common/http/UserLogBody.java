package cn.cjwddz.knowu.common.http;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Administrator on 2018/9/11.
 */

public class UserLogBody {
    public String type;
    public String userId;
    public JSONObject info;
    public String platform;

    public void setType(String type) {
        this.type = type;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setInfo(JSONObject info) {
        this.info = info;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
