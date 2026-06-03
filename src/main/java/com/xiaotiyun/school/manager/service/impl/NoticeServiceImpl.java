package com.xiaotiyun.school.manager.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.xiaotiyun.school.manager.basic.enums.FileRelevanceTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.MiniWxAppMessageType;
import com.xiaotiyun.school.manager.config.FileConfig;
import com.xiaotiyun.school.manager.config.WxMaProperties;
import com.xiaotiyun.school.manager.dao.ActivityStudentApplyReportDao;
import com.xiaotiyun.school.manager.model.dto.*;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.req.ActivityStudentApplyAdmittedListReqModel;
import com.xiaotiyun.school.manager.model.req.ActivityStudentApplyNotAdmittedListReqModel;
import com.xiaotiyun.school.manager.model.req.ActivityStudentApplyNotRegisteredListReqModel;
import com.xiaotiyun.school.manager.model.req.ActivityStudentApplyRegisteredListReqModel;
import com.xiaotiyun.school.manager.model.res.ActivityStudentApplyAdmittedListResModel;
import com.xiaotiyun.school.manager.model.res.ActivityStudentApplyNotAdmittedListResModel;
import com.xiaotiyun.school.manager.model.res.ActivityStudentApplyNotRegisteredListResModel;
import com.xiaotiyun.school.manager.model.res.ActivityStudentApplyRegisteredListResModel;
import com.xiaotiyun.school.manager.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 企业微信通知Service层实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
    private static final ExecutorService sendWechatNoticePool = new ThreadPoolExecutor(10, 15, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100));
    private static final Set<Long> processedNoticeIds = new HashSet<>();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日HH:mm");
    @Resource
    private SysFileRelevanceService sysFileRelevanceService;
    @Resource
    private SysFileService sysFileService;
    @Resource
    private EnterpriseWechatNoticeService enterpriseWechatNoticeService;
    @Resource
    private EnterpriseWechatService enterpriseWechatService;
    @Resource
    private EnterpriseWechatRelService enterpriseWechatRelService;
    @Resource
    private LeisureActivityRecordService leisureActivityRecordService;
    @Resource
    private ActivityStudentApplyReportDao activityStudentApplyReportDao;
    @Resource
    private StudentService studentService;
    @Resource
    private SchoolService schoolService;
    @Resource
    private FileConfig fileConfig;
    @Resource
    private WxMaProperties wxMaProperties;

    @Override
    public void sendEnterpriseWechatNotice() {
        // 查询状态为未发送(0)并且发送时间不为空的通知
        QueryWrapper<EnterpriseWechatNoticeEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(EnterpriseWechatNoticeEntity::getStatus, 0) // 未发送状态
                .le(EnterpriseWechatNoticeEntity::getSendTime, LocalDateTime.now()) // 发送时间小于等于当前时间
                .orderByAsc(EnterpriseWechatNoticeEntity::getCreateTime); // 按创建时间升序排列
        List<EnterpriseWechatNoticeEntity> notices = enterpriseWechatNoticeService.list(wrapper);
        if (CollectionUtils.isNotEmpty(notices)) {
            // 使用Set记录已处理的通知ID，防止重复处理
            for (EnterpriseWechatNoticeEntity notice : notices) {
                // 防止重复处理同一个通知
                if (processedNoticeIds.contains(notice.getId())) {
                    continue;
                }
                // 双重检查，确保通知仍然处于未发送状态
                EnterpriseWechatNoticeEntity currentNotice = enterpriseWechatNoticeService.getById(notice.getId());
                if (currentNotice != null && currentNotice.getStatus() == 0) {
                    // 使用线程池异步发送通知
                    CompletableFuture.runAsync(() -> {
                        sendNotice(currentNotice);
                    }, sendWechatNoticePool).whenComplete((res, ex) -> {
                        if (ex != null) {
                            log.error("异步发送通知任务执行结束!");
                        } else {
                            log.info("异步发送通知完成！");
                        }
                    });
                    processedNoticeIds.add(notice.getId());
                }
            }
        }
    }

    /**
     * 异步发送企业微信通知
     *
     * @param notice
     */
    @Override
    public void sendNotice(EnterpriseWechatNoticeEntity notice) {
        if (notice != null) {
            try {
                boolean isUpdate = true;
                // 根据通知类型处理不同的发送逻辑
                switch (notice.getNoticeType()) {
                    case 1: // 自定义通知
                        sendCustomizeNotice(notice);
                        break;
                    case 2: // 余暇活动
                        sendLeisureNotice(notice);
                        break;
                    case 3: // 健康申报
                        sendHealthDeclareNotice(notice);
                        break;
                    default:
                        isUpdate = false;
                        log.warn("未知的通知类型: {}", notice.getNoticeType());
                        break;
                }
                if (isUpdate) {
                    // 更新发送状态
                    notice.setStatus(1); // 1表示已发送
                    notice.setSendTime(LocalDateTime.now());
                    enterpriseWechatNoticeService.updateById(notice);
                    processedNoticeIds.remove(notice.getId());
                }
            } catch (Exception e) {
                log.error("发送企业微信通知失败，notice: {}", JSON.toJSONString(notice), e);
            }
        }
    }

    /**
     * 发送自定义通知
     *
     * @param notice 通知实体
     */
    private void sendCustomizeNotice(EnterpriseWechatNoticeEntity notice) {
        try {
            //解析筛选条件json
            EnterpriseWechatNoticeFilterValueDTO filterValueDTO = JSON.parseObject(notice.getFilterValue(), EnterpriseWechatNoticeFilterValueDTO.class);
            QueryWrapper<EnterpriseWechatRelEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(EnterpriseWechatRelEntity::getSchoolId, notice.getSchoolId())
                    .eq(EnterpriseWechatRelEntity::getSchoolYear, notice.getSchoolYear());
            boolean isSend = false;
            switch (filterValueDTO.getType()) {
                case 1:
                    //发送给某个学部，获取学部关联id
                    if (filterValueDTO.getDepartment() != null && filterValueDTO.getDepartment() > 0) {
                        isSend = true;
                        queryWrapper.lambda().eq(EnterpriseWechatRelEntity::getType, 5)
                                .eq(EnterpriseWechatRelEntity::getRelId, filterValueDTO.getDepartment());
                    }
                    break;
                case 2:
                    //发送给某个年级，获取年级关联id
                    if (filterValueDTO.getGradeId() != null && filterValueDTO.getGradeId() > 0) {
                        isSend = true;
                        queryWrapper.lambda().eq(EnterpriseWechatRelEntity::getType, 1)
                                .eq(EnterpriseWechatRelEntity::getRelId, filterValueDTO.getGradeId());
                    }
                    break;
                case 3:
                    //发送给某个班级，获取班级关联id
                    if (filterValueDTO.getClassId() != null && filterValueDTO.getClassId() > 0) {
                        isSend = true;
                        queryWrapper.lambda().eq(EnterpriseWechatRelEntity::getType, 2)
                                .eq(EnterpriseWechatRelEntity::getRelId, filterValueDTO.getClassId());
                    }
                    break;
            }
            if (isSend) {
                List<EnterpriseWechatRelEntity> wechatRelEntities = enterpriseWechatRelService.list(queryWrapper);
                if (CollectionUtils.isNotEmpty(wechatRelEntities)) {
                    WechatMessageDTO messageDTO = new WechatMessageDTO();
                    messageDTO.setTo_party(wechatRelEntities.stream().map(EnterpriseWechatRelEntity::getWxId).collect(Collectors.toList()));
                    handleTargetType(notice, messageDTO);
                    // 获取关联的图片文件
                    List<String> imageUrls = getFileUrls(notice.getId(), notice.getSchoolId());
                    File imageFile = null;
                    if (CollectionUtils.isNotEmpty(imageUrls)) {
                        imageFile = new File(fileConfig.getFileRootPath() + imageUrls.get(0));
                    }
                    // 准备图片文件
                    File finalImageFile;
                    if (imageFile != null && imageFile.exists()) {
                        // 使用上传的图片文件
                        finalImageFile = imageFile;
                    } else {
                        ClassPathResource resource = new ClassPathResource(wxMaProperties.getWechatNoticeIllustration());
                        if (resource.exists()) {
                            finalImageFile = resource.getFile();
                        } else {
                            // 如果默认图片也不存在，则不传图片
                            finalImageFile = null;
                        }
                    }
                    handleNotice(notice, messageDTO);
                    enterpriseWechatService.sendMiniWxAPPMessage(notice.getSchoolId(), notice.getNoticeType(), MiniWxAppMessageType.MINI_PROGRAM, finalImageFile, messageDTO);
                }
            }
        } catch (IOException e) {
            log.error("发送自定义通知失败", e);
            throw new RuntimeException(e);
        }
    }

    private void handleNotice(EnterpriseWechatNoticeEntity notice, WechatMessageDTO messageDTO) {
//        WechatMessageMiniprogramNewsDTO mpnews = new WechatMessageMiniprogramNewsDTO();
//        List<WechatMessageMiniprogramNewsArticlesDTO> articles = new ArrayList<>();
//        WechatMessageMiniprogramNewsArticlesDTO articlesDTO = new WechatMessageMiniprogramNewsArticlesDTO();
//        articlesDTO.setTitle(notice.getTitle());
//        articlesDTO.setContent(notice.getNoticeContent());
//        articles.add(articlesDTO);
//        mpnews.setArticles(articles);
//        messageDTO.setMpnews(mpnews);
        WechatMessageMiniprogramDTO miniprogram = new WechatMessageMiniprogramDTO();
        miniprogram.setTitle(notice.getTitle());
        messageDTO.setMiniprogram(miniprogram);
    }

    /**
     * 处理目标类型
     *
     * @param notice
     * @param messageDTO
     */
    private void handleTargetType(EnterpriseWechatNoticeEntity notice, WechatMessageDTO messageDTO) {
        switch (notice.getTargetType()) {
            case 1:
                //全部
                messageDTO.setRecv_scope(2);
                break;
            case 2:
                //学生
                messageDTO.setRecv_scope(1);
                break;
            case 3:
                //家长
                messageDTO.setRecv_scope(0);
                break;
        }
    }

    /**
     * 发送余暇活动通知
     *
     * @param notice 通知实体
     */
    private void sendLeisureNotice(EnterpriseWechatNoticeEntity notice) {
        try {
            LeisureActivityRecordEntity activityRecord = leisureActivityRecordService.getById(notice.getBusinessId());
            if (activityRecord != null) {
                //解析筛选条件json
                EnterpriseWechatNoticeFilterValueDTO filterValueDTO = JSON.parseObject(notice.getFilterValue(), EnterpriseWechatNoticeFilterValueDTO.class);
                QueryWrapper<EnterpriseWechatRelEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(EnterpriseWechatRelEntity::getSchoolId, notice.getSchoolId())
                        .eq(EnterpriseWechatRelEntity::getSchoolYear, notice.getSchoolYear());
                boolean isSend = false;
                switch (filterValueDTO.getType()) {
                    case 0:
                        //发送全部需要参与余暇活动的用户
                        isSend = true;
                        queryWrapper.lambda().eq(EnterpriseWechatRelEntity::getType, 5)
                                .eq(EnterpriseWechatRelEntity::getRelId, activityRecord.getDepartment());
                        break;
                    case 1:
                        //发送给已报名的用户
                        ActivityStudentApplyRegisteredListReqModel registeredReqModel = new ActivityStudentApplyRegisteredListReqModel();
                        registeredReqModel.setActivityId(activityRecord.getId());
                        List<ActivityStudentApplyRegisteredListResModel> registeredResModels = activityStudentApplyReportDao.registeredList(new ArrayList<>(), registeredReqModel);
                        if (CollectionUtils.isNotEmpty(registeredResModels)) {
                            List<Long> studentIds = registeredResModels.stream().map(ActivityStudentApplyRegisteredListResModel::getStudentId).collect(Collectors.toList());
                            isSend = true;
                            queryWrapper.lambda().eq(EnterpriseWechatRelEntity::getType, 3)
                                    .in(EnterpriseWechatRelEntity::getRelId, studentIds);
                        }
                        break;
                    case 2:
                        //发送给未报名的用户
                        ActivityStudentApplyNotRegisteredListReqModel notRegisteredReqModel = new ActivityStudentApplyNotRegisteredListReqModel();
                        notRegisteredReqModel.setActivityId(activityRecord.getId());
                        List<ActivityStudentApplyNotRegisteredListResModel> notRegisteredResModels = activityStudentApplyReportDao.notRegisteredList(activityRecord.getSchoolId(), activityRecord.getSchoolYear(), activityRecord.getDepartment(), new ArrayList<>(), notRegisteredReqModel);
                        if (CollectionUtils.isNotEmpty(notRegisteredResModels)) {
                            List<Long> studentIds = notRegisteredResModels.stream().map(ActivityStudentApplyNotRegisteredListResModel::getStudentId).collect(Collectors.toList());
                            isSend = true;
                            queryWrapper.lambda().eq(EnterpriseWechatRelEntity::getType, 3)
                                    .in(EnterpriseWechatRelEntity::getRelId, studentIds);
                        }
                        break;
                    case 3:
                        //发送给已录取的用户
                        ActivityStudentApplyAdmittedListReqModel admittedReqModel = new ActivityStudentApplyAdmittedListReqModel();
                        admittedReqModel.setActivityId(activityRecord.getId());
                        if (activityRecord.getSecondEndTime() != null) {
                            //二次报名
                            admittedReqModel.setType(2);
                        } else {
                            //一次报名
                            admittedReqModel.setType(1);
                        }
                        List<ActivityStudentApplyAdmittedListResModel> admittedResModels = activityStudentApplyReportDao.admittedList(new ArrayList<>(), admittedReqModel);
                        if (CollectionUtils.isNotEmpty(admittedResModels)) {
                            List<Long> studentIds = admittedResModels.stream().map(ActivityStudentApplyAdmittedListResModel::getStudentId).collect(Collectors.toList());
                            isSend = true;
                            queryWrapper.lambda().eq(EnterpriseWechatRelEntity::getType, 3)
                                    .in(EnterpriseWechatRelEntity::getRelId, studentIds);
                        }
                        break;
                    case 4:
                        //发送给未录取的用户
                        ActivityStudentApplyNotAdmittedListReqModel notAdmittedReqModel = new ActivityStudentApplyNotAdmittedListReqModel();
                        notAdmittedReqModel.setActivityId(activityRecord.getId());
                        if (activityRecord.getSecondEndTime() != null) {
                            //二次报名
                            notAdmittedReqModel.setType(2);
                        } else {
                            //一次报名
                            notAdmittedReqModel.setType(1);
                        }
                        List<ActivityStudentApplyNotAdmittedListResModel> notAdmittedResModels = activityStudentApplyReportDao.notAdmittedList(new ArrayList<>(), notAdmittedReqModel);
                        if (CollectionUtils.isNotEmpty(notAdmittedResModels)) {
                            List<Long> studentIds = notAdmittedResModels.stream().map(ActivityStudentApplyNotAdmittedListResModel::getStudentId).collect(Collectors.toList());
                            isSend = true;
                            queryWrapper.lambda().eq(EnterpriseWechatRelEntity::getType, 3)
                                    .in(EnterpriseWechatRelEntity::getRelId, studentIds);
                        }
                        break;
                }
                if (isSend) {
                    List<EnterpriseWechatRelEntity> wechatRelEntities = enterpriseWechatRelService.list(queryWrapper);
                    if (CollectionUtils.isNotEmpty(wechatRelEntities)) {
                        // 获取关联的图片文件
                        List<String> imageUrls = getFileUrls(notice.getId(), notice.getSchoolId());
                        File imageFile = null;
                        if (CollectionUtils.isNotEmpty(imageUrls)) {
                            imageFile = new File(fileConfig.getFileRootPath() + imageUrls.get(0));
                        }
                        // 准备图片文件
                        File finalImageFile;
                        if (imageFile != null && imageFile.exists()) {
                            // 使用上传的图片文件
                            finalImageFile = imageFile;
                        } else {
                            ClassPathResource resource = new ClassPathResource(wxMaProperties.getWechatNoticeIllustration());
                            if (resource.exists()) {
                                finalImageFile = resource.getFile();
                            } else {
                                // 如果默认图片也不存在，则不传图片
                                finalImageFile = null;
                            }
                        }
                        if (notice.getTargetType() == 1) {
                            //发送给全部，包括学生和家长，只需发送一次
                            WechatMessageDTO messageDTO = new WechatMessageDTO();
                            handleTargetType(notice, messageDTO);
                            messageDTO.setTo_party(wechatRelEntities.stream().map(EnterpriseWechatRelEntity::getWxId).collect(Collectors.toList()));
                            handleNotice(notice, messageDTO);
                            enterpriseWechatService.sendMiniWxAPPMessage(notice.getSchoolId(), notice.getNoticeType(), MiniWxAppMessageType.MINI_PROGRAM, finalImageFile, messageDTO);
                        } else {
                            //发送给学生或家长，需要分批处理
                            List<String> wxIds = wechatRelEntities.stream()
                                    .filter(wechatRelEntity -> wechatRelEntity.getType() == 3)
                                    .map(EnterpriseWechatRelEntity::getWxId)
                                    .collect(Collectors.toList());
                            // 分批发送，每批1000条
                            int batchSize = 1000;
                            for (int i = 0; i < wxIds.size(); i += batchSize) {
                                int endIndex = Math.min(i + batchSize, wxIds.size());
                                List<String> batchWxIds = wxIds.subList(i, endIndex);
                                WechatMessageDTO batchMessageDTO = new WechatMessageDTO();
                                batchMessageDTO.setMsgtype("miniprogram");
                                handleTargetType(notice, batchMessageDTO);
                                batchMessageDTO.setTo_student_userid(batchWxIds);
                                handleNotice(notice, batchMessageDTO);
                                enterpriseWechatService.sendMiniWxAPPMessage(notice.getSchoolId(), notice.getNoticeType(), MiniWxAppMessageType.MINI_PROGRAM, finalImageFile, batchMessageDTO);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("发送余暇活动通知失败", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送健康申报通知
     *
     * @param notice 通知实体
     */
    private void sendHealthDeclareNotice(EnterpriseWechatNoticeEntity notice) {
        try {
            //解析筛选条件json
            EnterpriseWechatNoticeFilterValueDTO filterValueDTO = JSON.parseObject(notice.getFilterValue(), EnterpriseWechatNoticeFilterValueDTO.class);
            QueryWrapper<StudentEntity> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(StudentEntity::getSchoolId, notice.getSchoolId())
                    .eq(StudentEntity::getStatus, 1)
                    .orderByAsc(StudentEntity::getId);
            if (filterValueDTO.getType() == 1) {
                //已申报
                wrapper.lambda().eq(StudentEntity::getIsHealthDeclared, 1);
            } else if (filterValueDTO.getType() == 2) {
                //未申报
                wrapper.lambda().eq(StudentEntity::getIsHealthDeclared, 0);
            }
            // 获取关联的图片文件
            List<String> imageUrls = getFileUrls(notice.getId(), notice.getSchoolId());
            File imageFile = null;
            if (CollectionUtils.isNotEmpty(imageUrls)) {
                imageFile = new File(fileConfig.getFileRootPath() + imageUrls.get(0));
            }
            // 准备图片文件
            File finalImageFile;
            if (imageFile != null && imageFile.exists()) {
                // 使用上传的图片文件
                finalImageFile = imageFile;
            } else {
                ClassPathResource resource = new ClassPathResource(wxMaProperties.getWechatNoticeIllustration());
                if (resource.exists()) {
                    finalImageFile = resource.getFile();
                } else {
                    // 如果默认图片也不存在，则不传图片
                    finalImageFile = null;
                }
            }
            // 分页查询学生，每页1000条
            int pageSize = 1000;
            int currentPage = 1;
            List<StudentEntity> pageStudentEntities;
            do {
                // 设置分页参数
                PageHelper.startPage(currentPage, pageSize);
                pageStudentEntities = studentService.list(wrapper);
                if (CollectionUtils.isNotEmpty(pageStudentEntities)) {
                    List<Long> studentIds = pageStudentEntities.stream().map(StudentEntity::getId).collect(Collectors.toList());
                    QueryWrapper<EnterpriseWechatRelEntity> queryWrapper = new QueryWrapper<>();
                    queryWrapper.lambda().eq(EnterpriseWechatRelEntity::getSchoolId, notice.getSchoolId())
                            .eq(EnterpriseWechatRelEntity::getSchoolYear, notice.getSchoolYear())
                            .in(EnterpriseWechatRelEntity::getRelId, studentIds)
                            .eq(EnterpriseWechatRelEntity::getType, 3);
                    List<EnterpriseWechatRelEntity> wechatRelEntities = enterpriseWechatRelService.list(queryWrapper);
                    if (CollectionUtils.isNotEmpty(wechatRelEntities)) {
                        //发送给学生或家长
                        List<String> wxIds = wechatRelEntities.stream().map(EnterpriseWechatRelEntity::getWxId).collect(Collectors.toList());
                        WechatMessageDTO messageDTO = new WechatMessageDTO();
                        handleTargetType(notice, messageDTO);
                        messageDTO.setTo_student_userid(wxIds);
                        handleNotice(notice, messageDTO);
                        enterpriseWechatService.sendMiniWxAPPMessage(notice.getSchoolId(), notice.getNoticeType(), MiniWxAppMessageType.MINI_PROGRAM, finalImageFile, messageDTO);
                    }
                }
                currentPage++;
            } while (pageStudentEntities.size() == pageSize);
            //发送完毕更新学校健康申报状态
            SchoolEntity school = schoolService.getById(notice.getSchoolId());
            school.setIsHealthDeclared(1);
            schoolService.updateById(school);
        } catch (IOException e) {
            log.error("发送健康申报通知失败", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取通知关联的文件URL列表
     *
     * @param businessId 业务ID（通知ID）
     * @param schoolId   学校ID
     * @return 文件URL列表
     */
    private List<String> getFileUrls(Long businessId, Long schoolId) {
        QueryWrapper<SysFileRelevanceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(SysFileRelevanceEntity::getBusinessId, businessId)
                .eq(SysFileRelevanceEntity::getSchoolId, schoolId)
                .eq(SysFileRelevanceEntity::getType, FileRelevanceTypeEnum.ENTERPRISE_WECHAT_NOTICE.getType());

        List<SysFileRelevanceEntity> fileRelevanceList = sysFileRelevanceService.list(queryWrapper);

        if (CollectionUtils.isEmpty(fileRelevanceList)) {
            return new ArrayList<>();
        }

        List<Long> fileIds = fileRelevanceList.stream()
                .map(SysFileRelevanceEntity::getFileId)
                .collect(Collectors.toList());

        List<SysFileEntity> fileList = sysFileService.listByIds(fileIds);

        return fileList.stream()
                .map(SysFileEntity::getPath)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createLeisureNotice(LeisureActivityRecordEntity entity) {
        // 检查活动是否未发布
        if (entity.getOpenWechatNotice() == 1 && entity.getStatus() != 0) {
            // 删除未执行的通知
            deleteLeisureNotice(entity);
            List<EnterpriseWechatNoticeEntity> insertNotices = new ArrayList<>();
            Date now = new Date();
            if (now.before(entity.getStartTime())) {
                //活动未开始，需要创建即将开始通知
                Instant instant = entity.getStartTime().toInstant();
                LocalDateTime startTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                // 设置筛选条件
                EnterpriseWechatNoticeFilterValueDTO filterValueDTO = new EnterpriseWechatNoticeFilterValueDTO();
                filterValueDTO.setType(1); // 发送给参与的学部
                filterValueDTO.setDepartment(entity.getDepartment());
                EnterpriseWechatNoticeEntity soonStartNotice = new EnterpriseWechatNoticeEntity();
                soonStartNotice.setSchoolId(entity.getSchoolId());
                soonStartNotice.setSchoolYear(entity.getSchoolYear());
                soonStartNotice.setBusinessId(entity.getId());
                soonStartNotice.setNoticeType(2); // 余暇活动
                soonStartNotice.setTargetType(1); // 全部
                soonStartNotice.setFilterValue(JSON.toJSONString(filterValueDTO));
                soonStartNotice.setTitle("余暇活动即将于" + sdf.format(entity.getStartTime()) + "开始报名");
//                soonStartNotice.setTitle("余暇活动报名即将开始");
//                soonStartNotice.setNoticeContent(entity.getName() + "即将于" + sdf.format(entity.getStartTime()) + "开始报名");
                if (entity.getStartNoticeTime() != null) {
                    //设置了即将开始通知发送时间
                    soonStartNotice.setSendTime(entity.getStartNoticeTime());
                } else {
                    // 活动开始前3天发送
                    soonStartNotice.setSendTime(startTime.minusDays(3));
                }
                insertNotices.add(soonStartNotice);
                //创建开始通知
                EnterpriseWechatNoticeEntity startNotice = new EnterpriseWechatNoticeEntity();
                startNotice.setSchoolId(entity.getSchoolId());
                startNotice.setSchoolYear(entity.getSchoolYear());
                startNotice.setBusinessId(entity.getId());
                startNotice.setNoticeType(2); // 余暇活动
                startNotice.setTargetType(1); // 全部
                startNotice.setFilterValue(JSON.toJSONString(filterValueDTO));
                startNotice.setTitle("余暇活动已开始报名，截止" + sdf.format(entity.getEndTime()));
//                startNotice.setTitle("余暇活动报名已开始");
//                startNotice.setNoticeContent(entity.getName() + "已开始报名，截止" + sdf.format(entity.getEndTime()));
                //开始通知发送时间
                startNotice.setSendTime(startTime);
                insertNotices.add(startNotice);
            }

            if (now.before(entity.getEndTime())) {
                //活动未结束，需要创建即将结束通知
                Instant instant = entity.getEndTime().toInstant();
                LocalDateTime endTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                EnterpriseWechatNoticeEntity soonEndNotice = new EnterpriseWechatNoticeEntity();
                soonEndNotice.setSchoolId(entity.getSchoolId());
                soonEndNotice.setSchoolYear(entity.getSchoolYear());
                soonEndNotice.setBusinessId(entity.getId());
                soonEndNotice.setNoticeType(2); // 余暇活动
                soonEndNotice.setTargetType(1); // 全部
                // 设置筛选条件
                EnterpriseWechatNoticeFilterValueDTO soonEndFilterValueDTO = new EnterpriseWechatNoticeFilterValueDTO();
                soonEndFilterValueDTO.setType(2); // 未报名
                soonEndNotice.setFilterValue(JSON.toJSONString(soonEndFilterValueDTO));
                soonEndNotice.setTitle("余暇活动将于" + sdf.format(entity.getEndTime()) + "截止报名");
//                soonEndNotice.setTitle("余暇活动报名即将截止");
//                soonEndNotice.setNoticeContent(entity.getName() + "即将于" + sdf.format(entity.getEndTime()) + "截止报名");
                if (entity.getEndNoticeTime() != null) {
                    //设置了即将截止通知发送时间
                    soonEndNotice.setSendTime(entity.getEndNoticeTime());
                } else {
                    // 活动截止前1天发送
                    soonEndNotice.setSendTime(endTime.minusDays(1));
                }
                insertNotices.add(soonEndNotice);
                //创建开始通知
                EnterpriseWechatNoticeEntity endNotice = new EnterpriseWechatNoticeEntity();
                endNotice.setSchoolId(entity.getSchoolId());
                endNotice.setSchoolYear(entity.getSchoolYear());
                endNotice.setBusinessId(entity.getId());
                endNotice.setNoticeType(2); // 余暇活动
                endNotice.setTargetType(1); // 全部
                // 设置筛选条件
                EnterpriseWechatNoticeFilterValueDTO filterValueDTO = new EnterpriseWechatNoticeFilterValueDTO();
                filterValueDTO.setType(1); // 发送给参与的学部
                filterValueDTO.setDepartment(entity.getDepartment());
                endNotice.setFilterValue(JSON.toJSONString(filterValueDTO));
                endNotice.setTitle("余暇活动已结束报名，请等待报名结果");
//                endNotice.setTitle("余暇活动报名已结束");
//                endNotice.setNoticeContent(entity.getName() + "已结束报名，请等待报名结果");
                //截止通知发送时间
                endNotice.setSendTime(endTime);
                insertNotices.add(endNotice);
            }
            if (CollectionUtils.isNotEmpty(insertNotices)) {
                enterpriseWechatNoticeService.saveBatch(insertNotices);
                insertNotices.forEach(notice -> {
                    if (notice.getSendTime().isBefore(LocalDateTime.now())) {
                        // 将该发送的使用线程池异步发送通知
                        CompletableFuture.runAsync(() -> {
                            sendNotice(notice);
                        }, sendWechatNoticePool).whenComplete((res, ex) -> {
                            if (ex != null) {
                                log.error("异步发送余暇活动结束通知任务执行结束!");
                            } else {
                                log.info("异步发送余暇活动结束通知完成！");
                            }
                        });
                        processedNoticeIds.add(notice.getId());
                    }
                });
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLeisureNotice(LeisureActivityRecordEntity entity) {
        QueryWrapper<EnterpriseWechatNoticeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EnterpriseWechatNoticeEntity::getBusinessId, entity.getId())
                .eq(EnterpriseWechatNoticeEntity::getNoticeType, 2)
                .eq(EnterpriseWechatNoticeEntity::getSchoolId, entity.getSchoolId())
                .eq(EnterpriseWechatNoticeEntity::getIsSystem, true)
                .gt(EnterpriseWechatNoticeEntity::getSendTime, LocalDateTime.now());
        List<EnterpriseWechatNoticeEntity> noticeList = enterpriseWechatNoticeService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(noticeList)) {
            // 删除所有发送时间晚于当前时间的通知
            List<Long> noticeIds = noticeList.stream().map(EnterpriseWechatNoticeEntity::getId).collect(Collectors.toList());
            enterpriseWechatNoticeService.removeByIds(noticeIds);
            // 同时删除相关的文件关联记录
            QueryWrapper<SysFileRelevanceEntity> fileRelevanceQueryWrapper = new QueryWrapper<>();
            fileRelevanceQueryWrapper.lambda()
                    .in(SysFileRelevanceEntity::getBusinessId, noticeIds)
                    .eq(SysFileRelevanceEntity::getType, FileRelevanceTypeEnum.ENTERPRISE_WECHAT_NOTICE.getType());
            sysFileRelevanceService.remove(fileRelevanceQueryWrapper);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendLeisureNotice(LeisureActivityRecordEntity entity, int status) {
        if (entity.getOpenWechatNotice() == 1) {
            //开启了企微通知的活动需要发企微通知
            List<EnterpriseWechatNoticeEntity> insertNotices = new ArrayList<>();
            if (status == 1) {
                //一次公布，需要发送报名成功、报名失败和未报名通知
                // 设置筛选条件
                //报名成功通知
                EnterpriseWechatNoticeEntity enrollSuccessNotice = new EnterpriseWechatNoticeEntity();
                enrollSuccessNotice.setSchoolId(entity.getSchoolId());
                enrollSuccessNotice.setSchoolYear(entity.getSchoolYear());
                enrollSuccessNotice.setBusinessId(entity.getId());
                enrollSuccessNotice.setNoticeType(2); // 余暇活动
                enrollSuccessNotice.setTargetType(1); // 全部
                EnterpriseWechatNoticeFilterValueDTO enrollSuccessFilterValueDTO = new EnterpriseWechatNoticeFilterValueDTO();
                enrollSuccessFilterValueDTO.setType(3); // 已录取
                enrollSuccessNotice.setFilterValue(JSON.toJSONString(enrollSuccessFilterValueDTO));
                enrollSuccessNotice.setTitle("已成功报名余暇活动");
//                enrollSuccessNotice.setTitle("余暇活动报名成功");
//                enrollSuccessNotice.setNoticeContent("已成功报名" + entity.getName());
                insertNotices.add(enrollSuccessNotice);
                //报名成功通知
                EnterpriseWechatNoticeEntity enrollFailNotice = new EnterpriseWechatNoticeEntity();
                enrollFailNotice.setSchoolId(entity.getSchoolId());
                enrollFailNotice.setSchoolYear(entity.getSchoolYear());
                enrollFailNotice.setBusinessId(entity.getId());
                enrollFailNotice.setNoticeType(2); // 余暇活动
                enrollFailNotice.setTargetType(1); // 全部
                EnterpriseWechatNoticeFilterValueDTO enrollFailNoticeFilterValueDTO = new EnterpriseWechatNoticeFilterValueDTO();
                enrollFailNoticeFilterValueDTO.setType(4); // 未录取
                enrollFailNotice.setFilterValue(JSON.toJSONString(enrollFailNoticeFilterValueDTO));
                enrollFailNotice.setTitle("未成功报名余暇活动，请等待二次报名");
//                enrollFailNotice.setTitle("余暇活动报名失败");
//                enrollFailNotice.setNoticeContent("未成功报名" + entity.getName() + "，请等待二次报名");
                enrollFailNotice.setSendTime(entity.getStartNoticeTime());
                insertNotices.add(enrollFailNotice);
                //报名成功通知
                EnterpriseWechatNoticeEntity notEnrollNotice = new EnterpriseWechatNoticeEntity();
                notEnrollNotice.setSchoolId(entity.getSchoolId());
                notEnrollNotice.setSchoolYear(entity.getSchoolYear());
                notEnrollNotice.setBusinessId(entity.getId());
                notEnrollNotice.setNoticeType(2); // 余暇活动
                notEnrollNotice.setTargetType(1); // 全部
                EnterpriseWechatNoticeFilterValueDTO notEnrollNoticeFilterValueDTO = new EnterpriseWechatNoticeFilterValueDTO();
                notEnrollNoticeFilterValueDTO.setType(2); // 未报名
                notEnrollNotice.setFilterValue(JSON.toJSONString(notEnrollNoticeFilterValueDTO));
                notEnrollNotice.setTitle("未参与报名余暇活动，请等待二次报名");
//                notEnrollNotice.setTitle("余暇活动未报名");
//                notEnrollNotice.setNoticeContent("未参与报名" + entity.getName() + "，请等待二次报名");
                notEnrollNotice.setSendTime(entity.getStartNoticeTime());
                insertNotices.add(notEnrollNotice);
            } else {
                //二次公布，需要发送报名成功通知
                //报名成功通知
                EnterpriseWechatNoticeEntity enrollSuccessNotice = new EnterpriseWechatNoticeEntity();
                enrollSuccessNotice.setSchoolId(entity.getSchoolId());
                enrollSuccessNotice.setSchoolYear(entity.getSchoolYear());
                enrollSuccessNotice.setBusinessId(entity.getId());
                enrollSuccessNotice.setNoticeType(2); // 余暇活动
                enrollSuccessNotice.setTargetType(1); // 全部
                EnterpriseWechatNoticeFilterValueDTO enrollSuccessFilterValueDTO = new EnterpriseWechatNoticeFilterValueDTO();
                enrollSuccessFilterValueDTO.setType(3); // 已录取
                enrollSuccessNotice.setFilterValue(JSON.toJSONString(enrollSuccessFilterValueDTO));
                enrollSuccessNotice.setTitle("已成功报名余暇活动");
//                enrollSuccessNotice.setTitle("余暇活动报名成功");
//                enrollSuccessNotice.setNoticeContent("已成功报名" + entity.getName());
                insertNotices.add(enrollSuccessNotice);
            }
            if (CollectionUtils.isNotEmpty(insertNotices)) {
                enterpriseWechatNoticeService.saveBatch(insertNotices);
                insertNotices.forEach(notice -> {
                    // 将该发送的使用线程池异步发送通知
                    CompletableFuture.runAsync(() -> {
                        sendNotice(notice);
                    }, sendWechatNoticePool).whenComplete((res, ex) -> {
                        if (ex != null) {
                            log.error("异步发送余暇活动报名成功通知任务执行结束!");
                        } else {
                            log.info("异步发送余暇活动报名成功通知完成！");
                        }
                    });
                    processedNoticeIds.add(notice.getId());
                });
            }
        }
    }
}