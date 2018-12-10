package cn.cjwddz.knowu.common.http;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Administrator on 2018/9/11.
 */

public class UserBody {
    public int phone;
    public String wx;
    public String nick;
    public JSONObject more;
    public void setPhone(int phone) {
        this.phone = phone;
    }

    public void setWx(String wx) {
        this.wx = wx;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setMore(JSONObject more) {
        this.more = more;
    }
}
