package cn.cjwddz.knowu.service;

/**
 * Created by Administrator on 2018/7/19.
 */

public class Constants {

    /**
     * 权限申请请求
     */
    public static final int CAMERA_REQUEST_CODE = 0x01;
    public static final int PERMISSION_REQUEST_COARSE_LOCATION = 0x01;
    /**
     * MtDevice 服务UUID
     */
    public static final String SERVICE_UUID="00002010-0000-1000-8000-00805f9b34fb";
    /**
     * MtDevice 特征值UUID
     */
    public static final String CHARACTER_UUID="00001092-0000-1000-8000-00805f9b34fb";

    /**
     * 网络访问内容
     * */
    public static final String ADD_SYSTEM_LOG_URL = "http://www.cjwddz.cn:8080/knowu/addSystemLog";
    public static final String ADD_USER_URL = "http://www.cjwddz.cn:8080/knowu/addUser";
    public static final String ADD_USER_LOG_URL = "http://www.cjwddz.cn:8080/knowu/addUserLog";





    //获取验证码
    public static final String GET_CODE = "https://cjwddz.cn/frealu/user/validPhone";
    //登录
    public static final String LOGIN = "https://cjwddz.cn/frealu/user/login";
    //注册
    public static final String REGIETER = "https://cjwddz.cn/frealu/user/register";
    //注销
    public static final String EXIT = "https://cjwddz.cn/frealu/user/logout";
    //获取用户某月每一天使用时长
    public static final String GET_M = " https://cjwddz.cn/frealu/user/record/getMonthReport";
    //获取用户每天使用报告
    public static final String GET_D = " https://cjwddz.cn/frealu/user/record/getReport";
    //上传设备使用记录
    public static final String ADD_USER_RECORD_URL  = "  https://cjwddz.cn/frealu/user/record/addRecord";
    //上传用户评价
    public static final String ADD_USER_FEEL_URL  = " https://cjwddz.cn/frealu/user/record/add";
    //上传用户信息
    public static final String ADD_USER_INFO_URL  = " https://cjwddz.cn/frealu/user/miniSetPersonInfo";
    //获取用户信息
    public static final String GET_USER_INFO_URL  = " https://cjwddz.cn/frealu/user/getUserInfo";
    //检查更新
    public static final String UPDATE  = "http://cjwddz.cn:6325/mock/43/frealu/user/record/update";

    //获取服务器签名
   public static final String GETSIGN = "http://cjwddz.cn:8360/admin/auth/getSign";

    //获取头像
    public static final String GETHEADER= " http://source.cjwddz.cn/userHeader/";

    //获取头像
    public static final String GETAPK= " http://source.cjwddz.cn/APK/FrealU.apk";


    public static final String HEADER_CONTENT_TYPE_KEY = "Content-Type";
    public static final String HEADER_CONTENT_TYPE_VAULE = "application/x-www-form-urlencoded";
    public static final String HEADER_CONTENT_TYPE_VAULE_JSON = "application/json";
}
