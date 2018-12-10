package cn.cjwddz.knowu.common.http;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Administrator on 2018/9/11.
 */

public class SystemLogBody {
    public String type;
    public String user;
    public String msg;
    public JSONObject info;
    public String call_stack;
    public String platform;




    public void setType(String type) {
        this.type = type;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setInfo(JSONObject info) {
        this.info = info;
    }

    public void setCall_stack(String call_stack) {
        this.call_stack = call_stack;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }


}
