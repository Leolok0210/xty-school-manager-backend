package com.xiaotiyun.school.manager.controller.grade;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.GradeStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

/**
 *
 */
@Api(tags = "成绩统计")
@RestController
@RequestMapping("/api/grade/statistics")
@Slf4j
public class GradeStatisticsController extends BasicController {

    @Resource
    private GradeStatisticsService gradeStatisticsService;

    @ApiOperation("各班平均分查询")
    @SaCheckPermission("grade:statistics:avgList")
    @PostMapping("/class/avg")
    public Result<List<GradeClassAvgResModel>> classAvgList(@Validated @RequestBody GradeClassAvgReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        reqModel.setUserId(getUserId());
        return Result.success(gradeStatisticsService.getGradeClassAvg(reqModel));
    }

    @ApiOperation("各班平均分导出")
    @SaCheckPermission("grade:statistics:avgExport")
    @PostMapping("/class/avg/export")
    public ResponseEntity<byte[]> classAvgExport(@Validated @RequestBody GradeClassAvgReqModel reqModel) throws UnsupportedEncodingException {
        reqModel.setSchoolId(getSchoolId());
        reqModel.setUserId(getUserId());
        return gradeStatisticsService.exportGradeClassAvg(reqModel);
    }

    @ApiOperation("不合格成绩查询")
    @SaCheckPermission("grade:statistics:flunkList")
    @PostMapping("/flunk")
    public Result<List<GradeFlunkResModel>> flunkList(@Validated @RequestBody GradeFlunkReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        reqModel.setUserId(getUserId());
        return Result.success(gradeStatisticsService.getGradeFlunk(reqModel));
    }

    @ApiOperation("不合格成绩导出")
    @SaCheckPermission("grade:statistics:flunkExport")
    @PostMapping("/flunk/export")
    public ResponseEntity<byte[]> flunkExport(@Validated @RequestBody GradeFlunkReqModel reqModel) throws UnsupportedEncodingException {
        reqModel.setSchoolId(getSchoolId());
        reqModel.setUserId(getUserId());
        return gradeStatisticsService.exportFlunkExport(reqModel);
    }

    @ApiOperation("优良成绩")
	@SaCheckPermission("grade:statistics:excellentAndGood")
	@PostMapping("/excellentAndGood")
	public Result<ExcellentAndGoodGradesResModel> excellentAndGood(HttpServletRequest request,
																   @Validated @RequestBody ExcellentAndGoodGradesReqModel reqModel) {
        reqModel.setUserId(getUserId());
		return Result.success(gradeStatisticsService.getExcellentAndGood(reqModel,getSchoolId(request)));
	}


	@ApiOperation("导出优良成绩")
	@SaCheckPermission("grade:statistics:excellentAndGood")
	@PostMapping("/exportExcellentAndGood")
	@SaIgnore
	public ResponseEntity<byte[]> exportExcellentAndGood(HttpServletRequest request,
																   @Validated @RequestBody ExcellentAndGoodGradesReqModel reqModel) {
		// 获取Excel数据并生成字节数组
        reqModel.setUserId(getUserId());
		byte[] excelBytes = gradeStatisticsService.exportExcellentAndGood(reqModel,getSchoolId(request));
		// 设置响应头
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", UUID.randomUUID()+".xlsx");
		return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
	}

    @ApiOperation("学年成绩统计查询")
    @SaCheckPermission("grade:statistics:yearList")
    @PostMapping("/year")
    public Result<GradeYearResModel> yearList(@Validated @RequestBody GradeYearReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        reqModel.setUserId(getUserId());
        return Result.success(gradeStatisticsService.getGradeYear(reqModel));
    }

    @ApiOperation("学年成绩统计导出")
    @SaCheckPermission("grade:statistics:yearExport")
    @PostMapping("/year/export")
    public ResponseEntity<byte[]> yearExport(@Validated @RequestBody GradeYearReqModel reqModel) throws UnsupportedEncodingException {
        reqModel.setSchoolId(getSchoolId());
        reqModel.setUserId(getUserId());
        return gradeStatisticsService.exportGradeYear(reqModel);
    }


    @ApiOperation("最高成绩")
    @SaCheckPermission("grade:statistics:topScore")
    @PostMapping("/topScore")
    public Result<GradesStatisticsExcelResModel> topScore(HttpServletRequest request,
                                                        @Validated @RequestBody TopScoreReqModel reqModel) {
        // 调用service层方法获取最高成绩数据
        reqModel.setUserId(getUserId());
        GradesStatisticsExcelResModel result = gradeStatisticsService.getTopScore(reqModel,getSchoolId(request));
        return Result.success(result);
    }

    @ApiOperation("导出最高成绩")
    @SaCheckPermission("grade:statistics:topScore")
    @PostMapping("/exportTopScore")
    public ResponseEntity<byte[]> exportTopScore(HttpServletRequest request,
                                            @Validated @RequestBody TopScoreReqModel reqModel) {
        // 获取Excel数据并生成字节数组
        reqModel.setUserId(getUserId());
        byte[] excelBytes = gradeStatisticsService.exportTopScore(reqModel,getSchoolId(request));
        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", UUID.randomUUID() + ".xlsx");
        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}
