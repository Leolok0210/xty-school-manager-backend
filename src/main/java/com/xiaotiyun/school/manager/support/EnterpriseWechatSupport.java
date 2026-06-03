package com.xiaotiyun.school.manager.support;


import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.Data;
import org.apache.commons.compress.utils.Charsets;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

@Data
@Component
public class EnterpriseWechatSupport {

    @Value("${wxApp.teacher.suiteId}")
    private String teacherSuiteId;

    @Value("${wxApp.teacher.suiteSecret}")
    private String teacherSuiteSecret;

    @Value("${wxApp.teacher.H5Url}")
    private String teacherH5Url;

    @Value("${wxApp.teacher.token}")
    private String teacherToken;

    @Value("${wxApp.teacher.AESKey}")
    private String teacherAESKey;

    @Value("${wxApp.teacher.corpId}")
    private String teacherCorpId;

    @Value("${wxApp.teacher.providerSecret}")
    private String teacherProviderSecret;

    @Value("${wxApp.teacher.name}")
    private String teacherAppName;

    @Value("${wxApp.student.suiteId}")
    private String studentSuiteId;

    @Value("${wxApp.student.suiteSecret}")
    private String studentSuiteSecret;

    @Value("${wxApp.student.H5Url}")
    private String studentH5Url;

    @Value("${wxApp.student.token}")
    private String studentToken;

    @Value("${wxApp.student.AESKey}")
    private String studentAESKey;

    @Value("${wxApp.student.corpId}")
    private String studentCorpId;

    public static final String BASE_URL = "https://qyapi.weixin.qq.com/cgi-bin/";

    public static final String GET_TOKEN_PATH = "gettoken";// 获取企业内部token
    public static final String GET_INTERNAL_USER_INFO_PATH = "auth/getuserinfo"; // 获取用户信息

    public static final String GET_SUITE_TOKEN_PATH = "service/get_suite_token";// 获取三方应用token
    public static final String GET_USER_INFO_PATH = "service/auth/getuserinfo3rd";// 获取三方用户信息

    public static final String GET_PROVIDER_TOKEN_PATH = "service/get_provider_token";// 获取服务商凭证
    public static final String GET_PERMANENT_CODE_PATH = "service/v2/get_permanent_code";// 获取永久授权码
    public static final String GET_AUTH_INFO_PATH = "service/v2/get_auth_info";// 获取授权企业信息
    public static final String GET_CORP_TOKEN_PATH = "service/get_corp_token";// 获取企业token
    public static final String GET_OPEN_CORP_ID_PATH = "service/corpid_to_opencorpid";// 企业corpid转服务商corpid

    public static final String MEDIA_UPLOAD_PATH = "media/upload";// 上传临时媒体文件
    public static final String SEND_MESSAGE_PATH = "externalcontact/message/send";// 发送消息

    public static final String GET_DEPARTMENT_LIST_PATH = "school/department/list";// 获取部门
    public static final String GET_USER_LIST_PATH = "school/user/list";// 获取部门学生列表
    public static final String CREATE_DEPARTMENT_PATH = "school/department/create";// 创建部门
    public static final String UPDATE_DEPARTMENT_PATH = "school/department/update";// 更新部门
    public static final String DELETE_DEPARTMENT_PATH = "school/department/delete";// 删除部门

    public static final String CREATE_USER_PATH = "school/user/create_student";// 创建学生
    public static final String UPDATE_USER_PATH = "school/user/update_student";// 更新学生
    public static final String DELETE_USER_PATH = "school/user/delete_student";// 删除学生
    public static final String BATCH_CREATE_STUDENT_PATH = "school/user/batch_create_student";// 批量创建学生
    public static final String BATCH_UPDATE_STUDENT_PATH = "school/user/batch_update_student";// 批量更新学生
    public static final String BATCH_DELETE_STUDENT_PATH = "school/user/batch_delete_student";// 批量删除学生

    public static final String CREATE_PARENT_PATH = "school/user/create_parent";// 创建家长
    public static final String UPDATE_PARENT_PATH = "school/user/update_parent";// 更新家长
    public static final String DELETE_PARENT_PATH = "school/user/delete_parent";// 删除家长
    public static final String BATCH_CREATE_PARENT_PATH = "school/user/batch_create_parent";// 批量创建家长
    public static final String BATCH_UPDATE_PARENT_PATH = "school/user/batch_update_parent";// 批量更新家长
    public static final String BATCH_DELETE_PARENT_PATH = "school/user/batch_delete_parent";// 批量删除家长

    public String getPost(String path, Object o) {
        return HttpUtil.post(BASE_URL + path, JSON.toJSONString(o));
    }

    public String getPost(String path, String accessToken, Object o) {
        return HttpUtil.post(BASE_URL + path + "?access_token=" + accessToken, JSON.toJSONString(o));
    }

    public String getGet(String path, Object o) {
        return HttpUtil.get(BASE_URL + path, JSON.parseObject(JSON.toJSONString(o)));
    }

    public String getPostMedia(String path, File file) throws IOException {
        return uploadMedia(BASE_URL + path, file, null);
    }

    public static String uploadMedia(String url, File file, String params) throws IOException {
        URL urlGet = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlGet.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("connection", "Keep-Alive");
//        conn.setRequestProperty("user-agent", DEFAULT_USER_AGENT);
        conn.setRequestProperty("Charsert", "UTF-8");
        // 定义数据分隔线
        String BOUNDARY = "----WebKitFormBoundaryiDGnV9zdZA1eM1yL";
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

        OutputStream out = new DataOutputStream(conn.getOutputStream());
        // 定义最后数据分隔线
        StringBuilder mediaData = new StringBuilder();
        mediaData.append("--").append(BOUNDARY).append("\r\n");
        mediaData.append("Content-Disposition: form-data;name=\"media\";filename=\"" + file.getName() + "\"\r\n");
        mediaData.append("Content-Type:application/octet-stream\r\n\r\n");
        byte[] mediaDatas = mediaData.toString().getBytes();
        out.write(mediaDatas);
        DataInputStream fs = new DataInputStream(Files.newInputStream(file.toPath()));
        int bytes = 0;
        byte[] bufferOut = new byte[1024];
        while ((bytes = fs.read(bufferOut)) != -1) {
            out.write(bufferOut, 0, bytes);
        }
        IOUtils.closeQuietly(fs);
        // 多个文件时，二个文件之间加入这个
        out.write("\r\n".getBytes());
        if (StringUtils.isNotEmpty(params)) {
            StringBuilder paramData = new StringBuilder();
            paramData.append("--").append(BOUNDARY).append("\r\n");
            paramData.append("Content-Disposition: form-data;name=\"description\";");
            byte[] paramDatas = paramData.toString().getBytes();
            out.write(paramDatas);
            out.write(params.getBytes(Charsets.UTF_8));
        }
        byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
        out.write(end_data);
        out.flush();
        IOUtils.closeQuietly(out);

        // 定义BufferedReader输入流来读取URL的响应
        InputStream in = conn.getInputStream();
        BufferedReader read = new BufferedReader(new InputStreamReader(in, Charsets.UTF_8));
        String valueString = null;
        StringBuffer bufferRes = null;
        bufferRes = new StringBuffer();
        while ((valueString = read.readLine()) != null) {
            bufferRes.append(valueString);
        }
        IOUtils.closeQuietly(in);
        // 关闭连接
        if (conn != null) {
            conn.disconnect();
        }
        return bufferRes.toString();
    }
}
