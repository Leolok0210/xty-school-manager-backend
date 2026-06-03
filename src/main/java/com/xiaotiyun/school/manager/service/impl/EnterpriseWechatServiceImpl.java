package com.xiaotiyun.school.manager.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.XML;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.*;
import com.xiaotiyun.school.manager.basic.exception.AesException;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.wx.WXBizMsgCrypt;
import com.xiaotiyun.school.manager.config.WxMaProperties;
import com.xiaotiyun.school.manager.dao.UserDao;
import com.xiaotiyun.school.manager.model.dto.*;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.*;
import com.xiaotiyun.school.manager.support.EnterpriseWechatSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EnterpriseWechatServiceImpl implements EnterpriseWechatService {

    @Resource
    AuthService authService;
    @Resource
    private UserDao userDao;
    @Resource
    private SchoolService schoolService;
    @Resource
    private StudentService studentService;
    @Resource
    private SysClassService sysClassService;
    @Resource
    private GradeGroupService gradeGroupService;
    @Resource
    private SystemSettingService systemSettingService;
    @Resource
    private SchoolWeixinRelevanceService schoolWeixinRelevanceService;

    @Resource
    UserWeixinRelevanceService userWeixinRelevanceService;

    @Resource
    LanguageUtil languageUtil;

    @Lazy
    @Resource
    EnterpriseWechatSupport support;

    @Autowired
    private WxMaProperties wxMaProperties;

    @Value("${file.fileRootPath}")
    private String fileRootPath;

    @Value("${H5Url}")
    private String H5Url;

    @Resource
    private WxAuthService wxAuthService;

    @Resource
    private EnterpriseWechatSynService enterpriseWechatSynService;


    @Resource
    private EnterpriseWechatSynRecordService enterpriseWechatSynRecordService;


    @Resource
    private EnterpriseWechatRelService enterpriseWechatRelService;

    private final Cache<String, String> enterpriseWechatSuiteTicketCache = Caffeine.newBuilder()
            .maximumSize(4) // 最大缓存条目
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .initialCapacity(4)
            .build();

    // suite_access_token 第三方应用凭证 key = Token-Teacher
    // access_token 企业token key = Token-Enterprise + schoolId
    private final Cache<String, String> enterpriseWechatTokenCache = Caffeine.newBuilder()
            .maximumSize(2000) // 最大缓存条目
            .expireAfterWrite(2, TimeUnit.HOURS)
            .initialCapacity(10)
            .build();

    private final Cache<String, Map<String, String>> enterpriseWechatUserCache = Caffeine.newBuilder()
            .maximumSize(1000) // 最大缓存条目
            .expireAfterWrite(4, TimeUnit.HOURS)
            .initialCapacity(10)
            .build();
    @Autowired
    private EnterpriseWechatSupport enterpriseWechatSupport;

    @Override
    public void saveSuiteTicket(HttpServletRequest request, String msgSignature, String timestamp, String nonce, String requestBody, boolean isTeacher) throws AesException {
        WXBizMsgCrypt wxcpt;
        if (isTeacher) {
            wxcpt = new WXBizMsgCrypt(support.getTeacherToken(), support.getTeacherAESKey(), support.getTeacherSuiteId());
        } else {
            wxcpt = new WXBizMsgCrypt(support.getStudentToken(), support.getStudentAESKey(), support.getStudentSuiteId());
        }
        String sEchoStr = wxcpt.DecryptMsg(msgSignature, timestamp, nonce, requestBody);
        log.info("企业微信回调suiteToken 解密后:{}", sEchoStr);
        cn.hutool.json.JSONObject jsonObject = XML.toJSONObject(sEchoStr).getJSONObject("xml");
        // 获取到的suiteTicket
        String suiteTicket = jsonObject.getStr("SuiteTicket");
        if (suiteTicket != null) {
            // 保存到缓存
            log.info("是否为教师：{}，保存到缓存 企微SuiteTicket:{}", isTeacher, suiteTicket);
            if (isTeacher) {
                enterpriseWechatSuiteTicketCache.put(EnterpriseWeChatCacheTypeEnum.S_TICKET_TEA.getCode(), suiteTicket);
            } else {
                enterpriseWechatSuiteTicketCache.put(EnterpriseWeChatCacheTypeEnum.S_TICKET_STU.getCode(), suiteTicket);
            }
        }
        // 获取到新增授权
        String InfoType = jsonObject.getStr("InfoType");
        if (StringUtils.isNotEmpty(InfoType) && InfoType.equals("create_auth")) {
            // 获取临时码
            String authCode = jsonObject.getStr("AuthCode");
            log.info("获取到新增授权，临时码:{}", authCode);
            getEntInfo(authCode);
        }
    }

    private void getEntInfo(String authCode) {
        Map<String, String> req = new HashMap<>();
        req.put("auth_code", authCode);
        try {
            SchoolWeixinRelevanceEntity schoolWeixinRelevanceEntity = new SchoolWeixinRelevanceEntity();
            // 获取企业加密ID和永久码
            String suiteAccessToken = getAndSaveToken(null, "", true);
            if (StringUtils.isBlank(suiteAccessToken)) {
                log.error("获取新增授权，获取suite_access_token失败！");
                return;
            }
            String resGetEnt = enterpriseWechatSupport.getPost(EnterpriseWechatSupport.GET_PERMANENT_CODE_PATH +
                    "?suite_access_token=" + suiteAccessToken, req);
            JSONObject resJson = JSON.parseObject(resGetEnt);
            log.info("获取到新增授权，企业永久码:{}",resJson);
            if (resJson.getInteger("errcode") != 0) {
                return;
            }
            String corpId = resJson.getJSONObject("auth_corp_info").getString("corpid");
            String permanentCode = resJson.getString("permanent_code");
            schoolWeixinRelevanceEntity.setCorpId(corpId);
            schoolWeixinRelevanceEntity.setPermanentCode(permanentCode);
            // 获取企业信息
            req.clear();
            req.put("auth_corpid",corpId);
            req.put("permanent_code",permanentCode);
            String resAuthInfo = enterpriseWechatSupport.getPost(EnterpriseWechatSupport.GET_AUTH_INFO_PATH +
                    "?suite_access_token=" + suiteAccessToken + "&debug=1", req);
            log.info("获取到新增授权，授权企业信息:{}",resAuthInfo);
            JSONObject resJsonAuthInfo = JSON.parseObject(resAuthInfo);
            if (resJsonAuthInfo.getInteger("errcode") != 0) {
                return;
            }
            // 获取授权应用id
            JSONObject authInfo = resJsonAuthInfo.getJSONObject("auth_info");
            JSONArray agent = authInfo.getJSONArray("agent");
            if (agent != null && !agent.isEmpty()) {
                for (int i = 0; i < agent.size(); i++) {
                    JSONObject agentInfo = agent.getJSONObject(i);
                    if (enterpriseWechatSupport.getTeacherAppName().equals(agentInfo.getString("name"))) {
                        String agentId = agentInfo.getString("agentid");
                        // 保存授权信息
                        schoolWeixinRelevanceEntity.setAgentId(agentId);
                        schoolWeixinRelevanceEntity.setAgentName(agentInfo.getString("name"));
                        break;
                    }
                }
            }
            // 保存授权信息
            List<SchoolWeixinRelevanceEntity> list = schoolWeixinRelevanceService.list(new LambdaQueryWrapper<SchoolWeixinRelevanceEntity>()
                    .eq(SchoolWeixinRelevanceEntity::getCorpId, corpId)
                    .eq(SchoolWeixinRelevanceEntity::getAppType, 2));
            if (CollectionUtils.isEmpty(list)) {
                schoolWeixinRelevanceEntity.setAppType(2);
                schoolWeixinRelevanceService.save(schoolWeixinRelevanceEntity);
            } else {
                schoolWeixinRelevanceEntity.setId(list.get(0).getId());
                schoolWeixinRelevanceService.updateById(schoolWeixinRelevanceEntity);
            }
        } catch (Exception e) {
            log.error("获取新增授权企业信息失败！", e);
        }
    }

    /**
     * 获取企业token
     * @param schoolId 学校ID
     * @return 企业token,可能为null
     */
    private String getAndSaveEntTokenBySchoolId(Long schoolId) {
        String entToken = enterpriseWechatTokenCache.getIfPresent(EnterpriseWeChatCacheTypeEnum.ENT_TOKEN.getCode() + schoolId);
        if (StringUtils.isBlank(entToken)) {
            // 获取学校关联信息
            SchoolWeixinRelevanceEntity schoolWeixinRelevanceEntity = schoolWeixinRelevanceService.getOne(new LambdaQueryWrapper<SchoolWeixinRelevanceEntity>()
                    .eq(SchoolWeixinRelevanceEntity::getSchoolId, schoolId));
            if (schoolWeixinRelevanceEntity == null) {
                return null;
            }
            // 内部应用
            if (schoolWeixinRelevanceEntity.getAppType() == 1) {
                // 获取学校的内部应用token
                String corpId = schoolWeixinRelevanceEntity.getCorpId();
                String appSecret = schoolWeixinRelevanceEntity.getAppSecret();
                entToken = getAndSaveAccessToken(corpId, appSecret);
                if (StringUtils.isNotBlank(entToken)) {
                    enterpriseWechatTokenCache.put(EnterpriseWeChatCacheTypeEnum.ENT_TOKEN.getCode() + schoolId, entToken);
                }
            }
            if (schoolWeixinRelevanceEntity.getAppType() == 2) {
                // 获取学校的三方信息
                String corpId = schoolWeixinRelevanceEntity.getCorpId();
                String permanentCode = schoolWeixinRelevanceEntity.getPermanentCode();
                String suiteAccessToken = getAndSaveToken(null, "", true);
                entToken = getAndSaveEntToken(corpId, permanentCode, suiteAccessToken);
                if (StringUtils.isNotBlank(entToken)) {
                    enterpriseWechatTokenCache.put(EnterpriseWeChatCacheTypeEnum.ENT_TOKEN.getCode() + schoolId, entToken);
                }
            }
        }
        return entToken;
    }

    private String getAndSaveAccessToken (String corpId, String appSecret) {
        Map<String, Object> req = new HashMap<>();
        req.put("corpid", corpId);
        req.put("corpsecret", appSecret);
        String resGetEntToken = enterpriseWechatSupport.getGet(EnterpriseWechatSupport.GET_TOKEN_PATH, req);
        JSONObject resJsonToken = JSON.parseObject(resGetEntToken);
        log.info("获取到新增授权，内部企业AccessToken:{}",resJsonToken);
        if (resJsonToken.getInteger("errcode") != 0) {
            return null;
        }
        return resJsonToken.getString("access_token");
    }

    private String getAndSaveEntToken(String corpId, String permanentCode, String suiteAccessToken) {
        Map<String, Object> req = new HashMap<>();
        req.put("auth_corpid", corpId);
        req.put("permanent_code", permanentCode);
        String resGetEntToken = enterpriseWechatSupport.getPost(EnterpriseWechatSupport.GET_CORP_TOKEN_PATH + "?suite_access_token=" + suiteAccessToken, req);
        JSONObject resJsonToken = JSON.parseObject(resGetEntToken);
        log.info("获取到新增授权，企业token:{}",resJsonToken);
        if (resJsonToken.getInteger("errcode") != 0) {
            return null;
        }
        return resJsonToken.getString("access_token");
    }

    @Override
    public Result<SchoolWeixinRelevanceCheckResModel> checkCorpId(String openCorpId, Long schoolId, Integer type){
        String corpId = null;
        if (type == 1) {
            // 内部应用,使用明文id不需要修改
            corpId = openCorpId;
        } else if (type == 2) {
            // 三方应用
            // 获取教师应用通用token
            Map<String, Object> req = new HashMap<>();
            req.put("corpid", enterpriseWechatSupport.getTeacherCorpId());
            req.put("provider_secret", enterpriseWechatSupport.getTeacherProviderSecret());
            String resSendMsg = enterpriseWechatSupport.getPost(EnterpriseWechatSupport.GET_PROVIDER_TOKEN_PATH + "?debug=1", req);
            log.info("获取到新增授权，获取provider_token:{}",resSendMsg);
            if (StringUtils.isBlank(resSendMsg)) {
                return Result.failed();
            }
            JSONObject resJson = JSON.parseObject(resSendMsg);
            String providerToken = resJson.getString("provider_access_token");
            // 根据入参的corpId，获取加密后的corpId
            req.clear();
            req.put("corpid",openCorpId);
            String resGetOpenCorpId = enterpriseWechatSupport.getPost(EnterpriseWechatSupport.GET_OPEN_CORP_ID_PATH +
                    "?provider_access_token=" + providerToken + "&debug=1", req);
            log.info("获取到新增授权，获取open_corpid:{}",resGetOpenCorpId);
            JSONObject resGetOpenCorpIdJson = JSON.parseObject(resGetOpenCorpId);
            if (resGetOpenCorpIdJson.getInteger("errcode") != 0) {
                return Result.failed();
            }
            corpId = resGetOpenCorpIdJson.getString("open_corpid");
        }
        // 对比企微配置的corpid,对上则关联
        List<SchoolWeixinRelevanceEntity> schoolWeixinRelevanceEntity = schoolWeixinRelevanceService.list(new LambdaQueryWrapper<SchoolWeixinRelevanceEntity>()
                        .eq(SchoolWeixinRelevanceEntity::getAppType, type)
                        .eq(SchoolWeixinRelevanceEntity::getCorpId, corpId));
        if (CollectionUtils.isEmpty(schoolWeixinRelevanceEntity)) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.NO_MATCHING_WECHAT_INFO));
        }
        if (schoolWeixinRelevanceEntity.get(0).getSchoolId() != null && schoolWeixinRelevanceEntity.get(0).getSchoolId() > 0) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.WECHAT_INFO_ALREADY_MATCHED));
        }
        SchoolWeixinRelevanceCheckResModel resModel = new SchoolWeixinRelevanceCheckResModel();
        resModel.setId(schoolWeixinRelevanceEntity.get(0).getId());
        resModel.setCorpName(schoolWeixinRelevanceEntity.get(0).getCorpName());
        return Result.success(resModel);
    }

    @Override
    public Result<SchoolWeixinRelevanceCheckResModel> bindCorpId(String id, Long schoolId) {
        // 校验企微信息
        SchoolWeixinRelevanceEntity schoolWeixinRelevanceEntity = schoolWeixinRelevanceService.getById(id);
        if (schoolWeixinRelevanceEntity == null) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.NO_MATCHING_WECHAT_INFO));
        }
        // 绑定企微
        schoolWeixinRelevanceEntity.setSchoolId(schoolId);
        schoolWeixinRelevanceService.updateById(schoolWeixinRelevanceEntity);
        // 更新学校信息
        SchoolEntity school = schoolService.getById(schoolId);
        school.setEntWechatType(schoolWeixinRelevanceEntity.getAppType());
        school.setEntWechatName(schoolWeixinRelevanceEntity.getCorpName());
        schoolService.updateById(school);
        // 拼接返回类
        SchoolWeixinRelevanceCheckResModel resModel = new SchoolWeixinRelevanceCheckResModel();
        resModel.setId(schoolWeixinRelevanceEntity.getId());
        resModel.setCorpName(schoolWeixinRelevanceEntity.getCorpName());
        return Result.success(resModel);
    }

    @Override
    public WechatMessageResDTO sendMiniWxAPPMessage(Long schoolId, int msgType, MiniWxAppMessageType miniWxAppMessageType, File file, WechatMessageDTO reqDTO) throws IOException {
        //获取企业token 取缓存，若没有，调用接口放在缓存中
        String corpToken = getAndSaveEntTokenBySchoolId(schoolId);
        if (StringUtils.isBlank(corpToken)) {
            return null;
        }
        // 获取企业的小程序应用Agentid
        List<SchoolWeixinRelevanceEntity> list = schoolWeixinRelevanceService.list(new LambdaQueryWrapper<SchoolWeixinRelevanceEntity>()
                .eq(SchoolWeixinRelevanceEntity::getSchoolId, schoolId));
        if (CollectionUtils.isEmpty(list) || StringUtils.isBlank(list.get(0).getAgentId())) {
            return null;
        }
        // 验证图片 最大10M
        if (file == null) {
            file = new File(fileRootPath + File.separator + "default.jpg");
        } else if (file.length() > 10 * 1024 * 1024) {
            // 太大获取默认图片
            file = new File(fileRootPath + File.separator + "default.jpg");
        }
        // 上传临时媒体文件
        String resMedia = enterpriseWechatSupport.getPostMedia(EnterpriseWechatSupport.MEDIA_UPLOAD_PATH +
                "?access_token=" + corpToken +
                "&type=image&debug=1", file);
        log.info("获取到新增授权，上传临时媒体文件:{}",resMedia);
        JSONObject resJsonMedia = JSON.parseObject(resMedia);
        if (resJsonMedia.getInteger("errcode") != 0) {
            return null;
        }
        String mediaId = resJsonMedia.getString("media_id");
        String url = "";
        if (miniWxAppMessageType == MiniWxAppMessageType.H5) {
            // 发送消息 msgType通知类型 1-自定义通知,2-余暇活动,3-健康申报
            url = H5Url + getMsgSceneByType(msgType);
            reqDTO.setAgentid(Integer.parseInt(list.get(0).getAgentId()));//从学校三方配置中获取
            reqDTO.getMpnews().getArticles().get(0).setThumb_media_id(mediaId);
        } else if (miniWxAppMessageType == MiniWxAppMessageType.MINI_PROGRAM) {
            // 发送消息 msgType通知类型 1-自定义通知,2-余暇活动,3-健康申报
            url = wxMaProperties.getIndexPath() + getMsgSceneByType(msgType);
            reqDTO.setAgentid(Integer.parseInt(list.get(0).getAgentId()));//从学校三方配置中获取
            reqDTO.getMiniprogram().setThumb_media_id(mediaId);
            reqDTO.getMiniprogram().setPagepath(url);
            reqDTO.getMiniprogram().setAppid(wxMaProperties.getAppid());
        }
        reqDTO.getMpnews().getArticles().get(0).setContent_source_url(url);
        String resSendMsg = enterpriseWechatSupport.getPost(EnterpriseWechatSupport.SEND_MESSAGE_PATH + "?access_token=" + corpToken + "&debug=1", reqDTO);
        log.info("获取到新增授权，发送消息:{}",resSendMsg);
        JSONObject resJsonMsg = JSON.parseObject(resSendMsg);
        if (resJsonMsg.getInteger("errcode") != 0) {
            return null;
        }
        return JSON.parseObject(resSendMsg, WechatMessageResDTO.class);
    }

    private String getMsgSceneByType(int msgType) {
        // 自定义时不需要拼接场景参数，1001-健康申报，1002-余暇活动
        if (msgType == 1) {
            return "?t=" + System.currentTimeMillis();
        } else if (msgType == 2) {
            return "?scene=1002&t=" + System.currentTimeMillis();
        } else if (msgType == 3) {
            return "?scene=1001&t=" + System.currentTimeMillis();
        }
        return "";
    }

    @Override
    public String getApplicationRegistration(WXAppRegisterReq reqModel, boolean isTeacher) throws AesException {
        WXBizMsgCrypt wxBizMsgCrypt;
        if (isTeacher) {
            wxBizMsgCrypt = new WXBizMsgCrypt(support.getTeacherToken(), support.getTeacherAESKey(), support.getTeacherCorpId());
        } else {
            wxBizMsgCrypt = new WXBizMsgCrypt(support.getStudentToken(), support.getStudentAESKey(), support.getStudentCorpId());
        }
        return wxBizMsgCrypt.VerifyURL(reqModel.getMsg_signature(), reqModel.getTimestamp(), reqModel.getNonce(), reqModel.getEchostr());
    }

    @Override
    public void getUserInfo(String code, boolean isTeacher, HttpServletResponse response) throws IOException {
        if (response != null) {
            String baseUrl = isTeacher ? support.getTeacherH5Url() : support.getStudentH5Url();
            String suiteTicket = getAndSaveToken(response, baseUrl, isTeacher);
            if (suiteTicket == null) {
                return;
            }
            EnterpriseWechatGetUserReq req = new EnterpriseWechatGetUserReq();
            req.setSuite_access_token(suiteTicket);
            req.setCode(code);
            String resultStr = support.getGet(EnterpriseWechatSupport.GET_USER_INFO_PATH, req);
            JSONObject result = JSON.parseObject(resultStr);
            if (result.getInteger("errcode") != 0) {
                log.error("是否为教师：{}，获取企微用户信息 失败！{}", isTeacher, resultStr);
                response.sendRedirect(baseUrl + "?message=" + getMessageByLanguage(languageUtil.getMessage(LanguageConstants.WX_LOGIN_ERROR)));
                return;
            }
            LambdaQueryWrapper<UserWeixinRelevanceEntity> where = Wrappers.<UserWeixinRelevanceEntity>lambdaQuery();
            where.eq(UserWeixinRelevanceEntity::getUserType, isTeacher ? 0 : 1);
            boolean isHasEnterprise = false;
            if (result.containsKey("userid")) {
                isHasEnterprise = true;
                where.eq(UserWeixinRelevanceEntity::getTriUserId, result.getString("userid"));
            } else if (result.containsKey("openid")) {
                where.eq(UserWeixinRelevanceEntity::getOpenId, result.getString("openid"));
            }
            List<UserWeixinRelevanceEntity> relevanceEntities = userWeixinRelevanceService.list(where);
            if (ObjectUtils.isEmpty(relevanceEntities)) {
                // 获取绑定需要的code返回
                getBindCode(response, result, isHasEnterprise, baseUrl);
            } else {
                if (isTeacher) {
                    // 教师绑定登录
                    UserEntity userEntity = userDao.selectById(relevanceEntities.get(0).getUserId());
                    if (userEntity == null) {
                        response.sendRedirect(baseUrl + "?message=" + getMessageByLanguage(languageUtil.getMessage(LanguageConstants.USER_NOT_EXISTS)));
                        return;
                    }
                    String token = authService.loginByUser(userEntity);
                    response.sendRedirect(baseUrl + "?token=" + token + "&time" + System.currentTimeMillis());
                } else {
                    // 学生绑定登录
                    StudentEntity student = studentService.getById(relevanceEntities.get(0).getUserId());
                    if (student == null) {
                        response.sendRedirect(baseUrl + "?message=" + getMessageByLanguage(languageUtil.getMessage(LanguageConstants.USER_NOT_EXISTS)));
                        return;
                    }
                    if (student.getStatus() != null && student.getStatus() != 1) {
                        // 若不在校，需要重新绑定
                        getBindCode(response, result, isHasEnterprise, baseUrl);
                        return;
                    }
                    String token = authService.loginByStudent(student);
                    response.sendRedirect(baseUrl + "?token=" + token + "&time" + System.currentTimeMillis());
                }
            }
        } else {
            log.error("是否为教师：{}，获取企微用户信息 获取HttpServletResponse失败！", isTeacher);
        }
    }

    @Override
    public void getUserInfoInternal(String code, String state, HttpServletResponse response) throws IOException {
        if (response != null) {
            List<SchoolWeixinRelevanceEntity> schoolRelevanceList = schoolWeixinRelevanceService.list(Wrappers.<SchoolWeixinRelevanceEntity>lambdaQuery()
                    .eq(SchoolWeixinRelevanceEntity::getAppType, 1)
                    .eq(SchoolWeixinRelevanceEntity::getSchoolId, state));
            if (ObjectUtils.isEmpty(schoolRelevanceList)) {
                log.error("该学校id{}没有绑定企微内部应用", state);
                return;
            }
            String baseUrl = schoolRelevanceList.get(0).getAgentUrl();
            // 获取token信息
            String accessToken = getAndSaveEntTokenBySchoolId(Long.valueOf(state));
            if (accessToken == null) {
                log.error("该学校id{}获取企微内部token失败", state);
                return;
            }
            Map<String, Object> req = new HashMap<>();
            req.put("access_token", accessToken);
            req.put("code", code);
            String resultStr = support.getGet(EnterpriseWechatSupport.GET_INTERNAL_USER_INFO_PATH, req);
            JSONObject result = JSON.parseObject(resultStr);
            if (result.getInteger("errcode") != 0) {
                log.error("获取企微内部用户信息 失败！{}", resultStr);
                response.sendRedirect(baseUrl + "?message=" + getMessageByLanguage(languageUtil.getMessage(LanguageConstants.WX_LOGIN_ERROR)) + "&appType=1");
                return;
            }
            LambdaQueryWrapper<UserWeixinRelevanceEntity> where = Wrappers.<UserWeixinRelevanceEntity>lambdaQuery();
            where.eq(UserWeixinRelevanceEntity::getUserType, 0);
            boolean isHasEnterprise = false;
            if (result.containsKey("userid")) {
                isHasEnterprise = true;
                where.eq(UserWeixinRelevanceEntity::getTriUserId, result.getString("userid"));
            } else if (result.containsKey("openid")) {
                where.eq(UserWeixinRelevanceEntity::getOpenId, result.getString("openid"));
            }
            List<UserWeixinRelevanceEntity> relevanceEntities = userWeixinRelevanceService.list(where);
            if (ObjectUtils.isEmpty(relevanceEntities)) {
                // 获取绑定需要的code返回
                getBindCode(response, result, isHasEnterprise, baseUrl);
            } else {
                // 教师绑定登录
                UserEntity userEntity = userDao.selectById(relevanceEntities.get(0).getUserId());
                if (userEntity == null) {
                    response.sendRedirect(baseUrl + "?message=" + getMessageByLanguage(languageUtil.getMessage(LanguageConstants.USER_NOT_EXISTS)) + "&appType=1");
                    return;
                }
                String token = authService.loginByUser(userEntity);
                response.sendRedirect(baseUrl + "?token=" + token + "&time" + System.currentTimeMillis());
            }
        } else {
            log.error("获取企微内部用户信息 获取HttpServletResponse失败！");
        }
    }

    private void getBindCode(HttpServletResponse response, JSONObject result, boolean isHasEnterprise, String baseUrl) throws IOException {
        Map<String, String> map = new HashMap<>();
        if (isHasEnterprise) {
            map.put("userId", result.getString("userid"));
            if (result.containsKey("corpid")) {
                map.put("corpId", result.getString("corpid"));
            }
            String uuid = UUID.randomUUID().toString();
            enterpriseWechatUserCache.put(uuid, map);
            response.sendRedirect(baseUrl + "?code=" + uuid + "&time" + System.currentTimeMillis());
        } else {
            map.put("openId", result.getString("openid"));
            String uuid = UUID.randomUUID().toString();
            enterpriseWechatUserCache.put(uuid, map);
            response.sendRedirect(baseUrl + "?code=" + uuid + "&time" + System.currentTimeMillis());
        }
    }

    @Override
    public LoginResModel bindAndLogin(WXBindAndLoginReq req) {
        LoginResModel res = authService.login(new LoginReqModel(req.getLoginName(), req.getPassword()));
        UserEntity userInfo = (UserEntity) StpUtil.getSession().get("userInfo");
        saveUserWeCharRelevance(userInfo.getId(), req.getCode(), true, null);
        return res;
    }

    @Override
    public LoginResModel stuBindAndLogin(WXStuBindAndLoginReq req) {
        // 校验学生是否存在
        StudentEntity student = studentService.getOne(Wrappers.<StudentEntity>lambdaQuery()
                .eq(StudentEntity::getSchoolId, req.getSchoolId())
                .eq(StudentEntity::getStudentNo, req.getStudentNo())
                .eq(StudentEntity::getStatus, 1));
        if (student == null) {
            throw new BusinessException(LanguageConstants.STUDENT_NUMBER_NOT_EXISTS);
        }
        // 根据语言查询不同名称，做校验
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        if (currentLanguage.equals(SchoolLanguageEnum.ZH_MO.getCode())) {
            if (!req.getName().equals(student.getChineseName())) {
                throw new BusinessException(LanguageConstants.STUDENT_NAME_ERROR);
            }
        } else {
            if (!req.getName().equals(student.getEnglishName())) {
                throw new BusinessException(LanguageConstants.STUDENT_NAME_ERROR);
            }
        }
        // 校验班级是否正确
        if (!req.getClassId().equals(student.getClassId())) {
            throw new BusinessException(LanguageConstants.GRADE_GROUP_CLASS_ERROR);
        }
        // 校验学生是否已经绑定
//        if (userWeixinRelevanceService.count(Wrappers.<UserWeixinRelevanceEntity>lambdaQuery()
//                .eq(UserWeixinRelevanceEntity::getUserId, student.getId())
//                .eq(UserWeixinRelevanceEntity::getUserType, 1)) > 0) {
//            throw new BusinessException(LanguageConstants.STUDENT_ALREADY_BOUND);
//        }
        if (StringUtils.isNotEmpty(req.getUserCode())) {
            saveUserWeCharRelevance(student.getId(), req.getUserCode(), false, req.getSchoolId());
        } else if (StringUtils.isNotEmpty(req.getCode())){
            WxMiniprogramLoginInfoDTO loginInfoDTO = wxAuthService.getOpenIdFromCode(req.getCode());
            if (loginInfoDTO == null) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.WX_LOGIN_ERROR));
            }
            if (userWeixinRelevanceService.count(Wrappers.<UserWeixinRelevanceEntity>lambdaQuery()
                    .eq(UserWeixinRelevanceEntity::getUserId, student.getId())
                    .eq(UserWeixinRelevanceEntity::getOpenId, loginInfoDTO.getOpenId())
                    .eq(UserWeixinRelevanceEntity::getUserType, 1)) == 0) {
                UserWeixinRelevanceEntity entity = new UserWeixinRelevanceEntity();
                entity.setUserId(student.getId());
                entity.setSchoolId(req.getSchoolId());
                entity.setOpenId(loginInfoDTO.getOpenId());
                entity.setUserType(1);
                userWeixinRelevanceService.save(entity);
            }
        }
//        else {
//            StudentEntity nowStudent = (StudentEntity) StpUtil.getSession().get("student");
//            if (nowStudent == null) {
//                throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
//            }
//            UserWeixinRelevanceEntity oldRel = userWeixinRelevanceService.getOne(Wrappers.<UserWeixinRelevanceEntity>lambdaQuery()
//                    .eq(UserWeixinRelevanceEntity::getUserId, nowStudent.getId())
//                    .eq(UserWeixinRelevanceEntity::getUserType, 1));
//            if (oldRel == null) {
//                throw new BusinessException(LanguageConstants.WECHAT_BIND_ERROR);
//            }
//            // 当前学生已绑定该学校，则不可重复绑定同一学校下的其他学生
//            if (student.getSchoolId().equals(nowStudent.getSchoolId())) {
//                throw new BusinessException(LanguageConstants.WECHAT_BIND_STUDENT_EXISTS);
//            }
//            // 检查当前企业微信用户绑定的所有学校，如果有重复，不能绑定
//            List<UserWeixinRelevanceEntity> list = userWeixinRelevanceService.list(Wrappers.<UserWeixinRelevanceEntity>lambdaQuery()
//                    .eq(StringUtils.isNotEmpty(oldRel.getOpenId()), UserWeixinRelevanceEntity::getOpenId, oldRel.getOpenId())
//                    .eq(StringUtils.isNotEmpty(oldRel.getTriUserId()), UserWeixinRelevanceEntity::getTriUserId, oldRel.getTriUserId())
//                    .eq(UserWeixinRelevanceEntity::getUserType, 1));
//            if (ObjectUtils.isNotEmpty(list)) {
//                Set<Long> studentIdList = list.stream().collect(Collectors.groupingBy(UserWeixinRelevanceEntity::getSchoolId)).keySet();
//                if (studentIdList.contains(student.getSchoolId())) {
//                    throw new BusinessException(LanguageConstants.WECHAT_BIND_STUDENT_EXISTS);
//                }
//            }
//            UserWeixinRelevanceEntity newRel = new UserWeixinRelevanceEntity();
//            newRel.setUserId(student.getId());
//            newRel.setCorpId(oldRel.getCorpId());
//            newRel.setOpenId(oldRel.getOpenId());
//            newRel.setTriUserId(oldRel.getTriUserId());
//            newRel.setUserType(1);
//            newRel.setSchoolId(req.getSchoolId());
//            userWeixinRelevanceService.save(newRel);
//        }
        LoginResModel res = new LoginResModel();
        res.setToken(authService.loginByStudent(student));
        sysClassService.getUserDetail(res, student);
        return res;
    }

    @Override
    public String studentUnbind(Long studentId, Long schoolId) {
        userWeixinRelevanceService.remove(Wrappers.<UserWeixinRelevanceEntity>lambdaQuery()
                .eq(UserWeixinRelevanceEntity::getSchoolId, schoolId)
                .eq(UserWeixinRelevanceEntity::getUserId, studentId)
                .eq(UserWeixinRelevanceEntity::getUserType, 1));
        return "success";
    }

    @Override
    public String studentGetBind(Long studentId) {
        List<UserWeixinRelevanceEntity> entities = userWeixinRelevanceService.list(Wrappers.<UserWeixinRelevanceEntity>lambdaQuery()
                .eq(UserWeixinRelevanceEntity::getUserId, studentId)
                .eq(UserWeixinRelevanceEntity::getUserType, 1));
        if (!CollectionUtils.isEmpty( entities))
        {
            UserWeixinRelevanceEntity rel = entities.get(0);
            return StringUtils.isNotEmpty(rel.getOpenId()) ? rel.getOpenId() : rel.getTriUserId();
        }
        return null;
    }

    @Override
    public List<StudentWeCharSchoolResModel> switchSchool() {
        StudentEntity nowStudent = (StudentEntity) StpUtil.getSession().get("student");
        if (nowStudent == null) {
            throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
        }
        UserWeixinRelevanceEntity rel = userWeixinRelevanceService.getOne(Wrappers.<UserWeixinRelevanceEntity>lambdaQuery()
                .eq(UserWeixinRelevanceEntity::getUserId, nowStudent.getId())
                .eq(UserWeixinRelevanceEntity::getUserType, 1));
        if (ObjectUtils.isEmpty(rel)) {
            throw new BusinessException(LanguageConstants.WECHAT_BIND_ERROR);
        }
        List<UserWeixinRelevanceEntity> relList = userWeixinRelevanceService.list(Wrappers.<UserWeixinRelevanceEntity>lambdaQuery()
                .eq(StringUtils.isNotBlank(rel.getCorpId()), UserWeixinRelevanceEntity::getCorpId, rel.getCorpId())
                .eq(StringUtils.isNotBlank(rel.getTriUserId()), UserWeixinRelevanceEntity::getTriUserId, rel.getTriUserId())
                .eq(StringUtils.isNotBlank(rel.getOpenId()), UserWeixinRelevanceEntity::getOpenId, rel.getOpenId())
                .eq(UserWeixinRelevanceEntity::getUserType, 1));
        if (ObjectUtils.isNotEmpty(relList)) {
            List<Long> studentIdList = relList.stream().map(UserWeixinRelevanceEntity::getUserId).collect(Collectors.toList());
            List<StudentEntity> studentList = studentService.list(Wrappers.<StudentEntity>lambdaQuery()
                    .in(StudentEntity::getId, studentIdList)
                    .eq(StudentEntity::getStatus, 1));
            if (ObjectUtils.isNotEmpty(studentList)) {
                List<Long> schoolIdList = studentList.stream().map(StudentEntity::getSchoolId).collect(Collectors.toList());
                List<SchoolEntity> schoolEntities = schoolService.listByIds(schoolIdList);
                if (ObjectUtils.isNotEmpty(schoolEntities)) {
                    Map<Long, SchoolEntity> schoolIdMap = schoolEntities.stream().collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
                    String currentLanguage = LanguageUtil.getCurrentLanguage();
                    return studentList.stream().map(student -> {
                        StudentWeCharSchoolResModel res = new StudentWeCharSchoolResModel();
                        res.setSchoolId(student.getSchoolId());
                        res.setSchoolName(schoolIdMap.get(student.getSchoolId()).getName());
                        res.setStudentId(student.getId());
                        res.setStudentName(currentLanguage.equals(SchoolLanguageEnum.ZH_MO.getCode()) ?
                                student.getChineseName() : student.getEnglishName());
                        return res;
                    }).collect(Collectors.toList());
                }
            }
        }
        return Collections.emptyList();
    }

    @Override
    public String changeWeCharStudentSchool(Long studentId) {
        // 原账号登录状态获取
        StudentEntity nowStudent = (StudentEntity) StpUtil.getSession().get("student");
        if (nowStudent == null) {
            throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
        }
        // 获取新
        StudentEntity student = studentService.getById(studentId);
        if (student == null) {
            throw new BusinessException(LanguageConstants.STUDENT_NOT_EXIST);
        }
        // 登出原账号
        StpUtil.logout();
        // 登录新账号，返回token
        return authService.loginByStudent(student);
    }

    @Override
    public MinigrogramUserResModel getUserInfo(String code, Long studentId) {
        MinigrogramUserResModel resModel = new MinigrogramUserResModel();
        WxMiniprogramLoginInfoDTO loginInfoDTO = wxAuthService.getOpenIdFromCode(code);
        if (loginInfoDTO == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.WX_LOGIN_ERROR));
        }
        LambdaQueryWrapper<UserWeixinRelevanceEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserWeixinRelevanceEntity::getOpenId, loginInfoDTO.getOpenId());
        queryWrapper.eq(UserWeixinRelevanceEntity::getUserType, 1);
        List<UserWeixinRelevanceEntity> relevanceEntities = userWeixinRelevanceService.list(queryWrapper);
        if (CollectionUtils.isEmpty(relevanceEntities)) {
            //需要绑定
            handlerBindData(resModel, loginInfoDTO);
            return resModel;
        } else {
            Map<Long, UserWeixinRelevanceEntity> studentIds = relevanceEntities.stream().collect(Collectors.toMap(UserWeixinRelevanceEntity::getUserId, Function.identity()));
            if (studentId == null || studentId == 0L || !studentIds.containsKey(studentId)) {
                // 若不在校，需要重新绑定
                handlerBindData(resModel, loginInfoDTO);
                return resModel;
            }
            // 学生绑定登录
            StudentEntity student = studentService.getById(studentId);
            if (student == null) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.USER_NOT_EXISTS));
            }
            if (student.getStatus() != null && student.getStatus() != 1) {
                // 若不在校，需要重新绑定
                handlerBindData(resModel, loginInfoDTO);
                return resModel;
            }
            String token = authService.loginByStudent(student);
            resModel.setBind(true);
            resModel.setToken(token);
            sysClassService.getUserDetail(resModel, student);
        }
        return resModel;
    }

    @Override
    public void authChannelGetUserInfo(String openId, MinigrogramAuthResModel resModel) {
        LambdaQueryWrapper<UserWeixinRelevanceEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserWeixinRelevanceEntity::getOpenId, openId);
        queryWrapper.eq(UserWeixinRelevanceEntity::getUserType, 1);
        List<UserWeixinRelevanceEntity> relevanceEntities = userWeixinRelevanceService.list(queryWrapper);
        if (CollectionUtils.isEmpty(relevanceEntities)) {
            //需要绑定
            getBindRes(openId, resModel);
        } else {
            // 学生绑定登录
            StudentEntity student = studentService.getById(relevanceEntities.get(0).getUserId());
            if (student == null) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.USER_NOT_EXISTS));
            }
            if (student.getStatus() != null && student.getStatus() != 1) {
                // 若不在校，需要重新绑定
                getBindRes(openId, resModel);
            }
            String token = authService.loginByStudent(student);
            resModel.setServiceBind(true);
            resModel.setToken(token);
            sysClassService.getUserDetail(resModel, student);
        }
    }

    private void getBindRes(String openId, MinigrogramAuthResModel resModel) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        Map<String, String> map = new HashMap<>();
        map.put("openId", openId);
        enterpriseWechatUserCache.put(uuid, map);
        resModel.setServiceBind(false);
        resModel.setUserCode(uuid);
    }

    private void handlerBindData(MinigrogramUserResModel resModel, WxMiniprogramLoginInfoDTO loginInfoDTO) {
        resModel.setBind(false);
        String uuid = UUID.randomUUID().toString();
        resModel.setCode(uuid);
        Map<String, String> map = new HashMap<>();
        map.put("openId", loginInfoDTO.getOpenId());
        enterpriseWechatUserCache.put(uuid, map);
    }

    private String getMessageByLanguage(String message) throws UnsupportedEncodingException {
        return URLEncoder.encode(message, "UTF-8");
    }

    private String getAndSaveToken(HttpServletResponse response, String baseH5Url, boolean isTeacher){
        try {
            // 去缓存中获取token
            String token;
            if (isTeacher) {
                token = enterpriseWechatTokenCache.getIfPresent(EnterpriseWeChatCacheTypeEnum.TOKEN_TEA.getCode());
            } else {
                token = enterpriseWechatTokenCache.getIfPresent(EnterpriseWeChatCacheTypeEnum.TOKEN_STU.getCode());
            }
            // 若不存在，再调用接口获取
            if (token == null) {
                EnterpriseWechatGetTokenReq req = new EnterpriseWechatGetTokenReq();
                String suite_ticket;
                if (isTeacher) {
                    suite_ticket = enterpriseWechatSuiteTicketCache.getIfPresent(EnterpriseWeChatCacheTypeEnum.S_TICKET_TEA.getCode());
                    req.setSuite_secret(support.getTeacherSuiteSecret());
                    req.setSuite_id(support.getTeacherSuiteId());
                } else {
                    suite_ticket = enterpriseWechatSuiteTicketCache.getIfPresent(EnterpriseWeChatCacheTypeEnum.S_TICKET_STU.getCode());
                    req.setSuite_secret(support.getStudentSuiteSecret());
                    req.setSuite_id(support.getStudentSuiteId());
                }
                if (StringUtils.isEmpty(suite_ticket)) {
                    log.error("是否为教师：{}，获取suite_ticket缓存失败！", isTeacher);
                    response.sendRedirect(baseH5Url + "?message=" + getMessageByLanguage(languageUtil.getMessage(LanguageConstants.WX_LOGIN_ERROR)));
                    return null;
                }
                req.setSuite_ticket(suite_ticket);
                String resultStr = support.getPost(EnterpriseWechatSupport.GET_SUITE_TOKEN_PATH, req);
                JSONObject result = JSON.parseObject(resultStr);
                log.info("是否为教师：{}，获取get_suite_token结果：{}", isTeacher, resultStr);
                if (!result.containsKey("suite_access_token")) {
                    log.error(String.format("是否为教师：%s，获取get_suite_token失败!%s", isTeacher, resultStr));
                    response.sendRedirect(baseH5Url + "?message=" + getMessageByLanguage(languageUtil.getMessage(LanguageConstants.WX_LOGIN_ERROR)));
                    return null;
                }
                token = result.getString("suite_access_token");
                // 保存到缓存
                if (isTeacher) {
                    enterpriseWechatTokenCache.put(EnterpriseWeChatCacheTypeEnum.TOKEN_TEA.getCode(), token);
                } else {
                    enterpriseWechatTokenCache.put(EnterpriseWeChatCacheTypeEnum.TOKEN_STU.getCode(), token);
                }
            }
            return token;
        } catch (Exception e) {
            log.error("获取企微教师端应用token失败！", e);
            return null;
        }
    }

    private void saveUserWeCharRelevance(Long userId, String code, boolean isTeacher, Long schoolId) {
        UserWeixinRelevanceEntity entity = new UserWeixinRelevanceEntity();
        entity.setUserId(userId);
        entity.setSchoolId(schoolId);
        Map<String, String> stringStringMap = enterpriseWechatUserCache.get(code, k -> {
            return null;
        });
        if (stringStringMap == null) {
            throw new BusinessException(LanguageConstants.BIND_TIMEOUT);
        }
        if (stringStringMap.containsKey("userId")) {
            if (stringStringMap.containsKey("corpId")) {
                entity.setCorpId(stringStringMap.get("corpId"));
            }
            entity.setTriUserId(stringStringMap.get("userId"));
        } else if(stringStringMap.containsKey("openId")) {
            entity.setOpenId(stringStringMap.get("openId"));
        }
        // 标识学生
        if (!isTeacher) {
            entity.setUserType(1);
        }
        if (userWeixinRelevanceService.count(Wrappers.<UserWeixinRelevanceEntity>lambdaQuery()
                .eq(UserWeixinRelevanceEntity::getUserId, userId)
                .eq(UserWeixinRelevanceEntity::getOpenId, entity.getOpenId())
                .eq(UserWeixinRelevanceEntity::getUserType, 1)) == 0) {
            userWeixinRelevanceService.save(entity);
        }
    }

    @Override
    public void createOrUpdateDepartment(Long schoolId, CreateOrUpdateBatchDepartmentDTO departmentDTO, Integer type, WechatBusinessTypeEnum businessTypeEnum,String schoolYear) {
        WechatDepartmentInfoDTO infoDTO = null;
        Long id = departmentDTO.getRelId();
        switch (type) {
            case 1:
                //班级
                SysClass sysClass = sysClassService.getById(id);
                if (sysClass != null) {
                    infoDTO = new WechatDepartmentInfoDTO();
                    infoDTO.setId(sysClass.getId().intValue());
                    infoDTO.setParentid(Integer.getInteger(departmentDTO.getParentId()));
                    infoDTO.setName(sysClass.getClassName());
                    infoDTO.setType(type);
                }
                break;
            case 2:
                //年级
                GradeGroup gradeGroup = gradeGroupService.getById(id);
                if (gradeGroup != null) {
                    infoDTO = new WechatDepartmentInfoDTO();
                    infoDTO.setId(gradeGroup.getId().intValue());
                    infoDTO.setParentid(Integer.getInteger(departmentDTO.getParentId()));
                    infoDTO.setName(gradeGroup.getGradeGroupName());
                    infoDTO.setType(type);
                }
                break;
            case 3:
                //学段
                DepartmentEnum departmentEnum = DepartmentEnum.getByCode(id.intValue());
                infoDTO = new WechatDepartmentInfoDTO();
                infoDTO.setId(id.intValue());
                infoDTO.setParentid(Integer.getInteger(departmentDTO.getParentId()));
                infoDTO.setName(departmentEnum.getDesc());
                infoDTO.setType(type);
                break;
//            case 4:
//                //校区
//
//                break;
        }
        if (infoDTO != null) {
            String accessToken = getAndSaveEntTokenBySchoolId(schoolId);
            String content = null;
            switch (businessTypeEnum) {
                case CREATE:
                    content = support.getPost(EnterpriseWechatSupport.CREATE_DEPARTMENT_PATH, accessToken, infoDTO);
                    log.info("企业微信创建部门结果：{}", content);
                    break;
                case UPDATE:
                    infoDTO.setId(Integer.parseInt(departmentDTO.getWxId()));
                    content = support.getPost(EnterpriseWechatSupport.UPDATE_DEPARTMENT_PATH, accessToken, infoDTO);
                    log.info("企业微信更新部门结果：{}", content);
                    break;
            }
            List<SynWxChatStatusUpdateDTO> recordList = new ArrayList<>();
            SynWxChatStatusUpdateDTO synWxChatStatusUpdateDTO = new SynWxChatStatusUpdateDTO();
            synWxChatStatusUpdateDTO.setRelId(id);
            recordList.add(synWxChatStatusUpdateDTO);
            synWxChatStatusUpdate(content, schoolId, type == 1 ? EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_CLASS : EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_LEVEL_GROUP,
                    recordList,null,recordList.size(),recordList.size());
        }
    }

    @Override
    public void createOrUpdateBatchDepartment(Long schoolId, List<CreateOrUpdateBatchDepartmentDTO> departmentDTOS, Integer type, WechatBusinessTypeEnum businessTypeEnum,String schoolYear) {

        List<WechatDepartmentInfoDTO> infoDTOS = new ArrayList<>();


        List<Long> ids = departmentDTOS.stream().map(CreateOrUpdateBatchDepartmentDTO::getRelId).collect(Collectors.toList());

        //tomap
        Map<Long, CreateOrUpdateBatchDepartmentDTO> map = departmentDTOS.stream().collect(Collectors.toMap(CreateOrUpdateBatchDepartmentDTO::getRelId,
                Function.identity(), (key1, key2) -> key1));
        switch (type) {
            case 1:
                //班级
                LambdaQueryWrapper<SysClass> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.in(SysClass::getId, ids);
                List<SysClass> sysClasses = sysClassService.list(queryWrapper);
                for (SysClass sysClass : sysClasses)
                {
                    WechatDepartmentInfoDTO infoDTO = new WechatDepartmentInfoDTO();
                    infoDTO.setId(sysClass.getId().intValue());
                    CreateOrUpdateBatchDepartmentDTO createOrUpdateBatchDepartmentDTO = map.get(sysClass.getId());
                    infoDTO.setParentid(Integer.getInteger(createOrUpdateBatchDepartmentDTO.getParentId()));
                    infoDTO.setName(sysClass.getClassName());
                    infoDTO.setType(type);
                    infoDTOS.add(infoDTO);
                }
                break;
            case 2:
                //年级
                LambdaQueryWrapper<GradeGroup> groupLambdaQueryWrapper = new LambdaQueryWrapper<>();
                groupLambdaQueryWrapper.in(GradeGroup::getId, ids);
                List<GradeGroup> gradeGroups = gradeGroupService.list(groupLambdaQueryWrapper);
                for (GradeGroup gradeGroup : gradeGroups)
                {
                    WechatDepartmentInfoDTO infoDTO = new WechatDepartmentInfoDTO();
                    infoDTO.setId(gradeGroup.getId().intValue());
                    CreateOrUpdateBatchDepartmentDTO createOrUpdateBatchDepartmentDTO = map.get(gradeGroup.getId());
                    infoDTO.setParentid(Integer.getInteger(createOrUpdateBatchDepartmentDTO.getParentId()));
                    infoDTO.setName(gradeGroup.getGradeGroupName());
                    infoDTO.setType(type);
                    infoDTOS.add(infoDTO);
                }
                break;
            case 3:
                //学段
                for (Long id : ids){
                    DepartmentEnum departmentEnum = DepartmentEnum.getByCode(id.intValue());
                    WechatDepartmentInfoDTO infoDTO = new WechatDepartmentInfoDTO();
                    infoDTO.setId(id.intValue());
                    CreateOrUpdateBatchDepartmentDTO createOrUpdateBatchDepartmentDTO = map.get(id);
                    infoDTO.setParentid(Integer.getInteger(createOrUpdateBatchDepartmentDTO.getParentId()));
                    infoDTO.setName(departmentEnum.getDesc());
                    infoDTO.setType(type);
                    infoDTOS.add(infoDTO);
                }
                break;
//            case 4:
//                //校区
//
//                break;
        }
        Long taskId = null;
        if (!CollectionUtils.isEmpty(infoDTOS)) {
            for (WechatDepartmentInfoDTO infoDTO : infoDTOS){
                long id = infoDTO.getId().longValue();
                String accessToken = getAndSaveEntTokenBySchoolId(schoolId);
                String content = null;
                switch (businessTypeEnum) {
                    case CREATE:
                        content = support.getPost(EnterpriseWechatSupport.CREATE_DEPARTMENT_PATH, accessToken, infoDTO);
                        log.info("企业微信创建部门结果：{}", content);
                        break;
                    case UPDATE:
                        CreateOrUpdateBatchDepartmentDTO createOrUpdateBatchDepartmentDTO = map.get(id);
                        infoDTO.setId(Integer.getInteger(createOrUpdateBatchDepartmentDTO.getWxId()));
                        content = support.getPost(EnterpriseWechatSupport.UPDATE_DEPARTMENT_PATH, accessToken, infoDTO);
                        log.info("企业微信更新部门结果：{}", content);
                        break;
                }
                List<SynWxChatStatusUpdateDTO> recordList = new ArrayList<>();
                SynWxChatStatusUpdateDTO synWxChatStatusUpdateDTO = new SynWxChatStatusUpdateDTO();
                synWxChatStatusUpdateDTO.setRelId(id);
                recordList.add(synWxChatStatusUpdateDTO);
                taskId = synWxChatStatusUpdate(content, schoolId, type == 1 ? EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_CLASS : EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_LEVEL_GROUP,
                        recordList,taskId,recordList.size(),infoDTOS.size());
            }
        }
    }

    @Override
    public void deleteDepartment(Long schoolId, Long id,Integer type,String schoolYear) {

        EnterpriseWechatRelEntity entity = enterpriseWechatRelService.get(schoolId, type, id,schoolYear);
        String wxId = entity.getWxId();
        Map<String, Object> params = new HashMap<>();
        params.put("id", Integer.getInteger(wxId));
        params.put("access_token", getAndSaveEntTokenBySchoolId(schoolId));
        String content = support.getGet(EnterpriseWechatSupport.DELETE_DEPARTMENT_PATH, params);
        log.info("企业微信删除部门结果：{}", content);
        List<SynWxChatStatusUpdateDTO> recordList = new ArrayList<>();
        SynWxChatStatusUpdateDTO synWxChatStatusUpdateDTO = new SynWxChatStatusUpdateDTO();
        synWxChatStatusUpdateDTO.setRelId(id);
        synWxChatStatusUpdateDTO.setThirdId(wxId);
        recordList.add(synWxChatStatusUpdateDTO);
        EnterpriseWxChatTypeEnum messageByCode = EnterpriseWxChatTypeEnum.getMessageByCode(type);
        //断言
        Assert.notNull(messageByCode, "类型不存在");
        synWxChatStatusUpdate(content, schoolId, messageByCode,
                recordList,null,recordList.size(),recordList.size());
    }

    @Override
    public void initDepartment(Long schoolId) {
        String accessToken = getAndSaveEntTokenBySchoolId(schoolId);
        //先初始化学部信息
        for (DepartmentEnum value : DepartmentEnum.values()) {
            WechatDepartmentInfoDTO infoDTO = new WechatDepartmentInfoDTO();
            infoDTO.setId(value.getCode());
            infoDTO.setParentid(0);
            infoDTO.setName(value.getDesc());
            infoDTO.setType(3);
            String content = support.getPost(EnterpriseWechatSupport.CREATE_DEPARTMENT_PATH, accessToken, infoDTO);
            log.info("企业微信创建学部{}结果：{}", value.getDesc(), content);
        }
        //创建级组
        QueryWrapper<GradeGroup> gradeWrapper = new QueryWrapper<>();
        gradeWrapper.lambda().eq(GradeGroup::getSchoolId, schoolId);
        List<GradeGroup> gradeGroupList = gradeGroupService.list(gradeWrapper);
        if (!CollectionUtils.isEmpty(gradeGroupList)) {
            gradeGroupList.forEach(gradeGroup -> {
                WechatDepartmentInfoDTO infoDTO = new WechatDepartmentInfoDTO();
                infoDTO.setId(gradeGroup.getId().intValue());
                infoDTO.setParentid(gradeGroup.getDepartment().intValue());
                infoDTO.setName(gradeGroup.getGradeGroupName());
                infoDTO.setType(2);
                String content = support.getPost(EnterpriseWechatSupport.CREATE_DEPARTMENT_PATH, accessToken, infoDTO);
                log.info("企业微信创建级组{}结果：{}", gradeGroup.getGradeGroupName(), content);
            });
            //创建班级
            QueryWrapper<SysClass> classWrapper = new QueryWrapper<>();
            classWrapper.lambda().eq(SysClass::getSchoolId, schoolId);
            List<SysClass> sysClasses = sysClassService.list(classWrapper);
            if (!CollectionUtils.isEmpty(sysClasses)) {
                sysClasses.forEach(sysClass -> {
                    WechatDepartmentInfoDTO infoDTO = new WechatDepartmentInfoDTO();
                    infoDTO.setId(sysClass.getId().intValue());
                    infoDTO.setParentid(sysClass.getGradeGroup().intValue());
                    infoDTO.setName(sysClass.getClassName());
                    infoDTO.setType(1);
                    String content = support.getPost(EnterpriseWechatSupport.CREATE_DEPARTMENT_PATH, accessToken, infoDTO);
                    log.info("企业微信创建班级{}结果：{}", sysClass.getClassName(), content);
                });
            }
        }
    }

    @Override
    public void createOrUpdateStudents(Long schoolId, List<SynWxChatStatusUpdateDTO> ids, WechatBusinessTypeEnum businessTypeEnum,String schoolYear) {
        // 分批处理，每批最多100个学生
        List<List<SynWxChatStatusUpdateDTO>> batches = new ArrayList<>();
        for (int i = 0; i < ids.size(); i += 100) {
            batches.add(ids.subList(i, Math.min(i + 100, ids.size())));
        }
        Map<Long, SynWxChatStatusUpdateDTO> idMap = ids.stream().collect(Collectors.toMap(SynWxChatStatusUpdateDTO::getRelId,
                Function.identity(), (x1, x2) -> x1));
        Long taskId = null;
        for (List<SynWxChatStatusUpdateDTO> batch : batches) {
            //tomao
            Map<Long, SynWxChatStatusUpdateDTO> map = batch.stream().collect(Collectors.toMap(SynWxChatStatusUpdateDTO::getRelId,
                    Function.identity(), (existing, replacement) -> existing));
            List<Long> studentIds = batch.stream().map(SynWxChatStatusUpdateDTO::getRelId).collect(Collectors.toList());
            List<StudentEntity> studentEntities = studentService.listByIds(studentIds);
            if (!CollectionUtils.isEmpty(studentEntities)) {
                List<WechatStudentInfoDTO> students = new ArrayList<>();
                studentEntities.forEach(studentEntity -> {
                    WechatStudentInfoDTO infoDTO = new WechatStudentInfoDTO();
                    infoDTO.setStudent_userid(map.get(studentEntity.getId()).getThirdId());
                    infoDTO.setName(studentEntity.getChineseName());
                    SynWxChatStatusUpdateDTO synWxChatStatusUpdateDTO = idMap.get(studentEntity.getId());
                    if (synWxChatStatusUpdateDTO != null) {
                        infoDTO.setMobile(synWxChatStatusUpdateDTO.getPhone());
                    }
                    List<Integer> classIds = new ArrayList<>();
                    //查询班级
                    EnterpriseWechatRelEntity entity = enterpriseWechatRelService.get(schoolId, EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_CLASS.getCode(),
                            studentEntity.getClassId(),schoolYear);
                    if (entity != null) {
                        classIds.add(Integer.parseInt(entity.getWxId()));
                    }
                    infoDTO.setDepartment(classIds);
                    students.add(infoDTO);
                });
                if (!CollectionUtils.isEmpty(students)) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("students", students);
                    String accessToken = getAndSaveEntTokenBySchoolId(schoolId);
                    String content = null;
                    switch (businessTypeEnum) {
                        case CREATE:
                            content = support.getPost(EnterpriseWechatSupport.BATCH_CREATE_STUDENT_PATH, accessToken, params);
                            log.info("企业微信批量创建学生结果：{}", content);
                            break;
                        case UPDATE:
                            content = support.getPost(EnterpriseWechatSupport.BATCH_UPDATE_STUDENT_PATH, accessToken, params);
                            log.info("企业微信批量更新学生结果：{}", content);
                            break;
                    }
                    taskId = synWxChatStatusUpdate(content,schoolId,EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_STUDENT,ids,taskId,batch.size(),ids.size());
                }
            }
        }
    }

    @Override
    public void deleteStudents(Long schoolId, List<Long> ids,String schoolYear) {
        String accessToken = getAndSaveEntTokenBySchoolId(schoolId);
        Map<String, Object> params = new HashMap<>();
        List<String> userIdList = ids.stream().map(String::valueOf).collect(Collectors.toList());
        params.put("useridlist", userIdList);
        String content = support.getPost(EnterpriseWechatSupport.BATCH_DELETE_STUDENT_PATH, accessToken, params);
        log.info("企业微信批量删除学生结果：{}", content);
        List<SynWxChatStatusUpdateDTO> recordList = new ArrayList<>();
        Map<Long, EnterpriseWechatRelEntity> map = enterpriseWechatRelService.list(schoolId, EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_STUDENT.getCode(), ids,schoolYear);
        map.forEach((id, entity) -> {
            SynWxChatStatusUpdateDTO synWxChatStatusUpdateDTO = new SynWxChatStatusUpdateDTO();
            synWxChatStatusUpdateDTO.setRelId(id);
            synWxChatStatusUpdateDTO.setThirdId(entity.getWxId());
            recordList.add(synWxChatStatusUpdateDTO);
        });
        synWxChatStatusUpdate(content,schoolId,EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_STUDENT,recordList,null,recordList.size(),recordList.size());
    }

    @Override
    public void initStudents(Long schoolId) {
        QueryWrapper<StudentEntity> studentWrapper = new QueryWrapper<>();
        studentWrapper.lambda().eq(StudentEntity::getSchoolId, schoolId);
        List<StudentEntity> studentEntities = studentService.list(studentWrapper);
        if (!CollectionUtils.isEmpty(studentEntities)) {
            // 分批处理，每批最多100个学生
            List<List<StudentEntity>> batches = new ArrayList<>();
            for (int i = 0; i < studentEntities.size(); i += 100) {
                batches.add(studentEntities.subList(i, Math.min(i + 100, studentEntities.size())));
            }

            // 处理每个批次
            for (List<StudentEntity> batch : batches) {
                List<WechatStudentInfoDTO> students = new ArrayList<>();
                batch.forEach(studentEntity -> {
                    WechatStudentInfoDTO infoDTO = new WechatStudentInfoDTO();
                    infoDTO.setStudent_userid(String.valueOf(studentEntity.getId()));
                    infoDTO.setName(studentEntity.getChineseName());
                    infoDTO.setMobile(studentEntity.getMobilePhone());
                    List<Integer> classIds = new ArrayList<>();
                    classIds.add(studentEntity.getClassId().intValue());
                    infoDTO.setDepartment(classIds);
                    students.add(infoDTO);
                });
                if (!CollectionUtils.isEmpty(students)) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("students", students);
                    String accessToken = getAndSaveEntTokenBySchoolId(schoolId);
                    String content = support.getPost(EnterpriseWechatSupport.BATCH_CREATE_STUDENT_PATH, accessToken, params);
                    log.info("企业微信批量创建学生结果：{}", content);
                }
            }
        }
    }

    @Override
    public void createOrUpdateParents(Long schoolId, List<WechatParentInfoDTO> parentInfoDTOList, WechatBusinessTypeEnum type,String schoolYear) {
        String accessToken = getAndSaveEntTokenBySchoolId(schoolId);
        Map<String, Object> params = new HashMap<>();
        params.put("parents", parentInfoDTOList);
        String content = null;
        switch (type) {
            case CREATE:
                content = support.getPost(EnterpriseWechatSupport.BATCH_CREATE_PARENT_PATH, accessToken, params);
                log.info("企业微信批量创建家长结果：{}", content);
                break;
            case UPDATE:
                content = support.getPost(EnterpriseWechatSupport.BATCH_UPDATE_PARENT_PATH, accessToken, params);
                log.info("企业微信批量更新家长结果：{}", content);
                break;
        }
        List<SynWxChatStatusUpdateDTO> recordList = new ArrayList<>();
        List<String> wxIds = parentInfoDTOList.stream().map(WechatParentInfoDTO::getParent_userid).collect(Collectors.toList());
        List<EnterpriseWechatRelEntity> enterpriseWechatRelEntities = enterpriseWechatRelService.listByWxIds(schoolId,
                EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_PARENT.getCode(), wxIds,schoolYear);
        enterpriseWechatRelEntities.forEach(enterpriseWechatRelEntity -> {
            SynWxChatStatusUpdateDTO synWxChatStatusUpdateDTO = new SynWxChatStatusUpdateDTO();
            synWxChatStatusUpdateDTO.setRelId(enterpriseWechatRelEntity.getId());
            synWxChatStatusUpdateDTO.setThirdId(enterpriseWechatRelEntity.getWxId());
            recordList.add(synWxChatStatusUpdateDTO);
        });
        synWxChatStatusUpdate(content,schoolId,EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_PARENT,recordList,null,recordList.size(),recordList.size());
    }

    @Override
    public void deleteParents(Long schoolId, List<String> ids,String schoolYear) {
        String accessToken = getAndSaveEntTokenBySchoolId(schoolId);
        Map<String, Object> params = new HashMap<>();
        params.put("useridlist", ids);
        String content = support.getPost(EnterpriseWechatSupport.BATCH_DELETE_PARENT_PATH, accessToken, params);
        log.info("企业微信批量删除家长结果：{}", content);
        List<SynWxChatStatusUpdateDTO> recordList = new ArrayList<>();
        List<EnterpriseWechatRelEntity> enterpriseWechatRelEntities = enterpriseWechatRelService.listByWxIds(schoolId,
                EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_PARENT.getCode(), ids,schoolYear);
        enterpriseWechatRelEntities.forEach(enterpriseWechatRelEntity -> {
            SynWxChatStatusUpdateDTO synWxChatStatusUpdateDTO = new SynWxChatStatusUpdateDTO();
            synWxChatStatusUpdateDTO.setRelId(enterpriseWechatRelEntity.getId());
            synWxChatStatusUpdateDTO.setThirdId(enterpriseWechatRelEntity.getWxId());
            recordList.add(synWxChatStatusUpdateDTO);
        });
        synWxChatStatusUpdate(content,schoolId,EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_PARENT,recordList,null,recordList.size(),recordList.size());
    }

    @Override
    public List<DepartmentResModelDTO> getDepartmentList(Long schoolId, String deptId) {
        String accessToken = getAndSaveEntTokenBySchoolId(schoolId);
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(deptId)) {
            params.put("id", deptId);
        }
        params.put("access_token", accessToken);
        String resStr = support.getGet(EnterpriseWechatSupport.GET_DEPARTMENT_LIST_PATH, params);
        JSONObject resJson = JSON.parseObject(resStr);
        if (resJson.getInteger("errcode") == 0) {
            JSONArray departmentList = resJson.getJSONArray("departments");
            return departmentList.toJavaList(DepartmentResModelDTO.class);
        }
        log.info("企业微信获取部门列表结果：{}", resStr);
        return new ArrayList<>();
    }

    @Override
    public List<StudentInfoResModelDTO> getDepartmentUser(Long schoolId, String deptId) {
        String accessToken = getAndSaveEntTokenBySchoolId(schoolId);
        Map<String, Object> params = new HashMap<>();
        params.put("department_id", deptId);
        params.put("access_token", accessToken);
        String resStr = support.getGet(EnterpriseWechatSupport.GET_USER_LIST_PATH, params);
        JSONObject resJson = JSON.parseObject(resStr);
        if (resJson.getInteger("errcode") == 0) {
            JSONArray students = resJson.getJSONArray("students");
            return students.toJavaList(StudentInfoResModelDTO.class);
        }
        log.info("企业微信获取部门学生列表结果：{}", resStr);
        return new ArrayList<>();
    }


    /**
     *
     * @param content
     * @param schoolId
     * @param typeEnum
     * @param size 这次的大小
     */
    private Long synWxChatStatusUpdate(String content,Long schoolId,EnterpriseWxChatTypeEnum typeEnum,
                                       List<SynWxChatStatusUpdateDTO> recordList,Long taskId,int size,int totalSize)
    {
        EnterpriseWechatSynEntity synEntity = null;
        if (taskId == null){
            synEntity = new EnterpriseWechatSynEntity();
            synEntity.setSchoolId(schoolId);
            synEntity.setType(typeEnum.getCode());
            synEntity.setTotalCount(totalSize);
            synEntity.setSuccessCount(0);
            synEntity.setFailCount(0);
            synEntity.setStatus(0);
            synEntity.setStartTime(new Date());
            Object loginId = StpUtil.getLoginId();
            if (loginId != null)
            {
                synEntity.setOpUserId(Long.parseLong(loginId.toString()));
            }
            enterpriseWechatSynService.save(synEntity);
        }else {
            synEntity = enterpriseWechatSynService.getById(taskId);
        }

        try {
            //tomap
            Map<String, Long> thirdIdMap = recordList.stream()
                    .filter(record -> StringUtils.isNotBlank(record.getThirdId()))
                    .collect(Collectors.toMap(SynWxChatStatusUpdateDTO::getThirdId, SynWxChatStatusUpdateDTO::getRelId));
            JSONObject jsonObject = JSONObject.parseObject(content);
            String errcode = jsonObject.get("errcode").toString();
            String errmsg = jsonObject.get("errmsg").toString();
            List<EnterpriseWechatSynRecordEntity> synRecordEntityList = new ArrayList<>();
            switch (typeEnum) {
                case RELEVANCE_TYPE_SECTION:
                case RELEVANCE_TYPE_LEVEL_GROUP:
                case RELEVANCE_TYPE_CLASS:
                    //部门
                    if ("0".equals(errcode)) {
                        synEntity.setStatus(2);
                        synEntity.setSuccessCount(synEntity.getSuccessCount() + size);
                    } else {
                        //errmsg
                        EnterpriseWechatSynRecordEntity synRecordEntity = new EnterpriseWechatSynRecordEntity();
                        synRecordEntity.setIncorrectReason(errmsg);
                        synRecordEntity.setTaskId(synEntity.getId());
                        synRecordEntity.setRelId(recordList.get(0).getRelId());
                        synRecordEntityList.add(synRecordEntity);
                        synEntity.setStatus(2);
                        synEntity.setFailCount(synEntity.getFailCount() + size);
                    }
                    break;
                case RELEVANCE_TYPE_STUDENT:
                    //学生
                    if ("0".equals(errcode)) {
                        synEntity.setStatus(2);
                        synEntity.setSuccessCount(synEntity.getSuccessCount() + size);
                    }else {
                        String result_list = jsonObject.get("result_list").toString();
                        if (result_list != null){
                            List<EnterpriseWxChatUserResDTO> enterpriseWxChatUserResDTOS = JSONObject.parseArray(result_list, EnterpriseWxChatUserResDTO.class);
                            for (EnterpriseWxChatUserResDTO enterpriseWxChatUserResDTO : enterpriseWxChatUserResDTOS) {
                                EnterpriseWechatSynRecordEntity synRecordEntity = new EnterpriseWechatSynRecordEntity();
                                synRecordEntity.setIncorrectReason(enterpriseWxChatUserResDTO.getErrmsg());
                                synRecordEntity.setTaskId(synEntity.getId());
                                Long aLong = thirdIdMap.get(enterpriseWxChatUserResDTO.getStudent_userid());
                                synRecordEntity.setRelId(aLong == null ? 0 : aLong);
                                synRecordEntityList.add(synRecordEntity);
                            }
                        }else {
                            synEntity.setStatus(2);
                        }
                    }
                    break;
                case RELEVANCE_TYPE_PARENT:
                    //家长
                    if ("0".equals(errcode)) {
                        synEntity.setStatus(2);
                        synEntity.setSuccessCount(synEntity.getSuccessCount() + size);
                    }else {
                        String result_list = jsonObject.get("result_list").toString();
                        if (result_list != null){
                            List<EnterpriseWxChatParentResDTO> enterpriseWxChatParentResDTOS = JSONObject.parseArray(result_list, EnterpriseWxChatParentResDTO.class);
                            for (EnterpriseWxChatParentResDTO enterpriseWxChatParentResDTO : enterpriseWxChatParentResDTOS) {
                                EnterpriseWechatSynRecordEntity synRecordEntity = new EnterpriseWechatSynRecordEntity();
                                synRecordEntity.setIncorrectReason(enterpriseWxChatParentResDTO.getErrmsg());
                                synRecordEntity.setTaskId(synEntity.getId());
                                Long aLong = thirdIdMap.get(enterpriseWxChatParentResDTO.getParent_userid());
                                synRecordEntity.setRelId(aLong == null ? 0 : aLong);
                                synRecordEntityList.add(synRecordEntity);
                            }
                        }else {
                            synEntity.setStatus(2);
                        }
                    }
                    break;
            }
            if (!synRecordEntityList.isEmpty()){
                enterpriseWechatSynRecordService.saveBatch(synRecordEntityList);
            }
        }catch (Exception e)
        {
            log.error("同步微信失败",e);
            synEntity.setStatus(2);
            synEntity.setFailCount(synEntity.getFailCount() +size);
        }
        synEntity.setEndTime(new Date());
        enterpriseWechatSynService.updateById(synEntity);

        return synEntity.getId();
    }

}
