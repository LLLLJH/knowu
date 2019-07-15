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
    //public static final String ADD_USER_URL = "http://www.cjwddz.cn:8080/knowu/addUser";
    //public static final String ADD_USER_LOG_URL = "http://www.cjwddz.cn:8080/knowu/addUserLog";





    //获取验证码
    public static final String GET_CODE = "http://47.107.78.54:8001/frealu/user/validPhone";
    //登录
    public static final String LOGIN = "http://47.107.78.54:8001/frealu/user/login";
    //注册
    public static final String REGIETER = "http://47.107.78.54:8001/frealu/user/register";
    //注销
    public static final String EXIT = "https://cjwddz.cn/frealu/user/logout";

    //上传设备使用记录
    public static final String ADD_USER_RECORD_URL  = "http://47.107.78.54:8001/frealu/user/record/addRecord";
    //上传用户评价
    public static final String ADD_USER_FEEL_URL  = "http://47.107.78.54:8001/frealu/user/record/add";
    //上传用户信息
    public static final String ADD_USER_INFO_URL  = "http://47.107.78.54:8001/frealu/user/miniSetPersonInfo";
    //获取用户信息
    public static final String GET_USER_INFO_URL  = "http://47.107.78.54:8001/frealu/user/getUserInfo";

    //获取服务器签名
   public static final String GETSIGN = "http://cjwddz.cn:8360/admin/auth/getSign";

    //获取头像
    public static final String GETHEADER= " http://head.mothtech.com/userHeader/";

    //检查更新
    public static final String UPDATE  = "http://head.mothtech.com/version/version.json";

    //获取APP
    public static final String GETAPK= " http://head.mothtech.com/apk/FrealU.apk";

    //获取当年十二个月份数据
    public static final String GET_YEAR_OF_MONTH = "http://47.107.78.54:8001/frealu/user/record/getStatisticsReport";
    //获取当年其中一个月数据
    public static final String GET_MONTH = "http://47.107.78.54:8001/frealu/user/record/getAnalysisReport";
    //获取某个月每一天数据
    public static final String GET_MONTH_OF_DAY = "http://47.107.78.54:8001/frealu/user/record/getStatisticsReport";
    //获取某一天数据
    public static final String GET_DAY = "http://47.107.78.54:8001/frealu/user/record/getAnalysisReport";

    public static final String HEADER_CONTENT_TYPE_KEY = "Content-Type";
    public static final String HEADER_CONTENT_TYPE_VAULE = "application/x-www-form-urlencoded";
    public static final String HEADER_CONTENT_TYPE_VAULE_JSON = "application/json";
}
