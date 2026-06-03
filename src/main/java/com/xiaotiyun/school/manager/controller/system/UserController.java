package com.xiaotiyun.school.manager.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.GenderEnum;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.ExcelStyleUtils;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.handler.UserImportTemplateCustomWriteHandler;
import com.xiaotiyun.school.manager.handler.UserImportTemplateHeaderHandler;
import com.xiaotiyun.school.manager.model.entity.DeptEntity;
import com.xiaotiyun.school.manager.model.entity.UserGroupEntity;
import com.xiaotiyun.school.manager.model.excel.UserImportHeaderModel;
import com.xiaotiyun.school.manager.model.excel.UserImportHeaderPtModel;
import com.xiaotiyun.school.manager.model.excel.UserImportHeaderUsModel;
import com.xiaotiyun.school.manager.model.req.UserAddReqModel;
import com.xiaotiyun.school.manager.model.req.UserDeleteReqModel;
import com.xiaotiyun.school.manager.model.req.UserQueryReqModel;
import com.xiaotiyun.school.manager.model.req.UserUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.LoginResModel;
import com.xiaotiyun.school.manager.model.res.TeacherListResModel;
import com.xiaotiyun.school.manager.model.res.UserDetailResModel;
import com.xiaotiyun.school.manager.model.res.UserMobileCheckResModel;
import com.xiaotiyun.school.manager.service.AuthService;
import com.xiaotiyun.school.manager.service.DeptService;
import com.xiaotiyun.school.manager.service.UserGroupService;
import com.xiaotiyun.school.manager.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@Api(tags = "用户管理")
public class UserController extends BasicController {
    @Resource
    private UserService userService;
    @Resource
    private AuthService authService;
    @Resource
    private LanguageUtil languageUtil;
    @Resource
    private UserGroupService userGroupService;
    @Resource
    private DeptService deptService;

    @PostMapping
    @ApiOperation("新增用户")
    @SaCheckPermission("system:user:add")
    public Result<Void> add(HttpServletRequest request, @Valid @RequestBody UserAddReqModel reqModel) {
        userService.addUser(reqModel, getSchoolId(request));
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改用户")
    @SaCheckPermission("system:user:edit")
    public Result<Void> update(HttpServletRequest request, @Valid @RequestBody UserUpdateReqModel reqModel) {
        userService.updateUser(reqModel, getSchoolId(request));
        return Result.success();
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除用户",
            notes = "预删除时，返回中Data中：" +
                    "1-只有一个部门；" +
                    "2-有多个部门，当前为主部门；" +
                    "3-有多个部门，当前非主部门；" +
                    "4-选择了根目录")
    @SaCheckPermission("system:user:del")
    public Result<Integer> deleteUser(HttpServletRequest request,@Valid @RequestBody UserDeleteReqModel reqModel) {
        Long schoolId = getSchoolId(request);
        if (ObjectUtils.isEmpty(reqModel.getId()) || ObjectUtils.isEmpty(schoolId)) {
            return Result.failed(ResultCode.FAILED.getCode(),languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
        }
        if (reqModel.getIsPre() == 0 && ObjectUtils.isEmpty(reqModel.getIsAll())){
            return Result.failed(ResultCode.FAILED.getCode(),languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
        }
        if (!ObjectUtils.isEmpty(reqModel.getIsAll()) && reqModel.getIsAll() == 0 && reqModel.getDeptId() == 0L){
            return Result.failed(ResultCode.FAILED.getCode(),languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
        }
        reqModel.setSchoolId(schoolId);
        return userService.deleteUser(reqModel);
    }

    @GetMapping("/{id}")
    @ApiOperation("查看用户详情")
    @SaCheckPermission("system:user:query")
    public Result<UserDetailResModel> getDetail(HttpServletRequest request, @PathVariable Long id) {
        return Result.success(userService.getUserDetail(id, getSchoolId(request)));
    }

    @PostMapping("/info")
    @ApiOperation("登录后获取用户信息")
    public Result<LoginResModel> wxGetLoginData() {
        return Result.success(authService.wxGetLoginData());
    }

    @GetMapping
    @ApiOperation("查询用户列表")
    @SaCheckPermission("system:user:query")
    public Result<PageInfo<UserDetailResModel>> list(HttpServletRequest request,
                                                     @Valid UserQueryReqModel reqModel) {
        return Result.success(userService.getUserPage(reqModel, getSchoolId(request)));
    }

    @GetMapping("/school/{schoolId}/teachers")
    @ApiOperation("查询学校下的对应的全部老师")
    public Result<List<TeacherListResModel>> listTeachersBySchool(HttpServletRequest request, @PathVariable Long schoolId) {
        if (ObjectUtils.isEmpty(schoolId)) {
            return Result.failed(ResultCode.VALIDATE_FAILED);
        }
        return Result.success(userService.getTeachersBySchool(schoolId));
    }

    @PutMapping("/{id}/password/reset")
    @ApiOperation("重置密码")
    @SaCheckPermission("system:user:edit")
    public Result<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.success();
    }

    @GetMapping("/exists/{mobile}")
    @ApiOperation("查询手机号是否存在")
    @SaCheckPermission("system:user:query")
    public Result<UserMobileCheckResModel> isMobileExists(HttpServletRequest request,
                                                          @PathVariable String mobile) {
        return Result.success(userService.checkMobileExists(mobile, getSchoolId(request)));
    }

    @ApiOperation("导入模板下载")
    @GetMapping("/template/download")
    @SaCheckPermission("system:user:import")
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        // 获取学校语言设置信息
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        if (StringUtils.isEmpty(currentLanguage)) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        SchoolLanguageEnum languageEnum = SchoolLanguageEnum.getDefValue(currentLanguage);
        if (languageEnum == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        ExcelWriter excelWriter = null;
        try {
            // 必须先设置响应头再获取输出流
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            excelWriter = EasyExcel.write(response.getOutputStream()).build();
            WriteSheet writeSheet;
            List<String> phoneAreas = new ArrayList<>();
            phoneAreas.add("86");
            phoneAreas.add("853");
            phoneAreas.add("852");
            //获取学校用户组信息
            List<String> userGroups = new ArrayList<>();
            QueryWrapper<UserGroupEntity> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(UserGroupEntity::getSchoolId, getSchoolId())
                    .or()
                    .eq(UserGroupEntity::getSchoolId, 0);
            List<UserGroupEntity> userGroupList = userGroupService.list(wrapper);
            if (CollectionUtils.isNotEmpty(userGroupList)) {
                userGroups = userGroupList.stream().map(UserGroupEntity::getName).collect(Collectors.toList());
            }
            // 获取学校部门信息
            List<DeptEntity> entities = deptService.list(Wrappers.<DeptEntity>lambdaQuery().eq(DeptEntity::getSchoolId, getSchoolId()));
            List<String> deptList = new ArrayList<>();
            if (!ObjectUtils.isEmpty(entities)){
                deptList = entities.stream().map(DeptEntity::getName).collect(Collectors.toList());
            }
            List<String> genders = new ArrayList<>(GenderEnum.allValues(languageEnum));
            switch (languageEnum) {
                case EN_US:
                    response.setHeader("Content-Disposition", "attachment; filename=User Import Template.xlsx");
                    writeSheet = EasyExcel.writerSheet()
                            .head(UserImportHeaderUsModel.class)
                            .registerWriteHandler(new UserImportTemplateCustomWriteHandler("Note:\n" +
                                    "1. Fields with dropdown options must use the dropdown selection\n" +
                                    "2. All fields marked as \"Required\" must be filled/selected\n" +
                                    "3. Mobile number: Must select the phone number region before entering the number\n" +
                                    "4. User ID: Can contain numbers and letters, up to 20 characters\n" +
                                    "5. Username：8-20 characters, must contain both letters and numbers"))
                            .registerWriteHandler(ExcelStyleUtils.getHeadStyleStrategy())
                            .registerWriteHandler(new UserImportTemplateHeaderHandler(userGroups, phoneAreas, deptList, genders))
                            .build();
                    break;
                case PT_PT:
                    response.setHeader("Content-Disposition", "attachment; filename=Modelo de Importação de Usuários.xlsx");
                    writeSheet = EasyExcel.writerSheet()
                            .head(UserImportHeaderPtModel.class)
                            .registerWriteHandler(new UserImportTemplateCustomWriteHandler("Observação:\n" +
                                    "1. Campos com opções suspensas devem usar a seleção suspensa\n" +
                                    "2. Todos os campos marcados como \"Obrigatório\" devem ser preenchidos/selecionados\n" +
                                    "3. Número de celular: Deve selecionar o país/região antes de inserir o número\n" +
                                    "4. ID do usuário: Pode conter números e letras, até 20 caracteres\n" +
                                    "5.Nome de usuário：8-20 caracteres, deve conter letras e números"))
                            .registerWriteHandler(ExcelStyleUtils.getHeadStyleStrategy())
                            .registerWriteHandler(new UserImportTemplateHeaderHandler(userGroups, phoneAreas, deptList, genders))
                            .build();
                    break;
                default:
                    response.setHeader("Content-Disposition", "attachment; filename=用戶導入模板.xlsx");
                    writeSheet = EasyExcel.writerSheet()
                            .head(UserImportHeaderModel.class)
                            .registerWriteHandler(new UserImportTemplateCustomWriteHandler("注：\n" +
                                    "1. 具有下拉選項的字段務必使用下拉選項\n" +
                                    "2. 所有標注“必填”的字段必須填寫/選擇\n" +
                                    "3. 手機號碼：填寫手機號必須先選擇手機號地區\n" +
                                    "4. 用戶編號：可包含數字、字母，不超過20字\n" +
                                    "5. 用戶名：8-20個字符，必須包含字母+數字"))
                            .registerWriteHandler(ExcelStyleUtils.getHeadStyleStrategy())
                            .registerWriteHandler(new UserImportTemplateHeaderHandler(userGroups, phoneAreas, deptList, genders))
                            .build();
                    break;
            }
            writeSheet.setRelativeHeadRowIndex(1);
            excelWriter.write(new ArrayList<>(), writeSheet);
        } finally {
            // 千万别忘记finish 会帮忙关闭流
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }

    @ApiOperation("导入用户")
    @PostMapping("/import")
    @SaCheckPermission("system:user:import")
    public Result<Long> importUser(@ApiParam("Excel文件") @RequestPart("uploadFile") MultipartFile file) {
        try {
            Long importId = userService.importUser(getSchoolId(), file);
            return Result.success(importId);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(ResultCode.FILE_UPLOAD_FAILED.getMessageCode()) + ":" + e.getMessage());
        }
    }

    @ApiOperation("导出用户")
    @GetMapping("/export")
    @SaCheckPermission("system:user:export")
    public Result<String> exportUser(@Valid UserQueryReqModel reqModel) {
        return Result.success(userService.exportUser(getSchoolId(), reqModel));
    }
}