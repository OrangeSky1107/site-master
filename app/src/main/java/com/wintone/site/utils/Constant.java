package com.wintone.site.utils;

public class Constant {

    // 测试接口地址 http://192.168.1.76/gdb-api/
    // http://127.0.0.1:8005/version/updatePassword
    // 谭皓本地地址 http://192.168.1.30:8005/
    public static final String BASE_URL = "http://192.168.1.38:19999";

    //修改密码
    public static final String UPDATE_PASSWORD_URL = "/version/updatePassword";

    //用户登录 /appUser/appLogin
    public static final String USER_LOGIN_URL = "/appUser/appLogin";

    public static final String USER_TOKEN = "user_token";

    public static final String USER_NAME = "user_name";

    public static final String CONSTRUCTION_ID = "construction_id";

    public static final String HEADER_IMAGE = "header_image";

    public static final String IS_MANAGER = "is_manager";

    public static final String ORG_ID = "org_id";

    public static final String PROJECT_ID = "project_id";

    public static final String SEX = "sex";

    public static final String STATUS = "status";

    public static final String USER_TYPE = "user_type";

    public static final String FACE_URL = "face_url";

    //首页人员情况接口
    public static final String HOME_PAGER_URL = "/appUser/personnel";

    //所属项目下拉
    public static final String PROJECT_LIST_URL = "/appProject/getProjectList";

    //所属分包商列表
    public static final String CONSTRUCTION_LIST_URL = "/appConstruction/getConstList";

    //班组下拉列表接口
    public static final String TEAM_LIST_URL = "/appTeam/getTeamList";

    //热门工种下拉列表接口
    public static final String DICTIONARIES_HOTDIC_URL = "/appDictionaries/hotDic";

    //图片上传地址 user/upload
    public static final String USER_UPLOAD_URL = BASE_URL + "/user/upload";

    //人员信息录入
    public static final String WORKERS_SAVEORUPDATE_URL = "/appWorkers/workersSaveOrUpdate";

    //小米5s BqkBdwpRbi6iJWEE8BcgUBddrfXAqrUJ9iRhaxJGhCxc
    public static final String APP_ID = "839bFTR4q4EbqXJBhZQjXR6ArtoJqRNw1vwuV4wzSo8Z";
    //小米5s BQV74Rd5ERqun64QdrjZ7SWKEohTfnShLdQ6KhPYy3jc
    public static final String SDK_KEY = "8THqDze4XoHRiztyyt3Ry1pwkvzGGLXF5cLxLLhtGeha";


}
