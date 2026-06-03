package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.VolunteerEntity;
import com.xiaotiyun.school.manager.model.req.VolunteerPageReqModel;
import com.xiaotiyun.school.manager.model.req.VolunteerSaveReqModel;
import com.xiaotiyun.school.manager.model.res.VolunteerResModel;
import com.xiaotiyun.school.manager.model.res.VolunteerStudentSumResModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface VolunteerService extends IService<VolunteerEntity> {
    PageInfo<VolunteerResModel> page(VolunteerPageReqModel reqModel);

    PageInfo<VolunteerResModel> pageByStudent(VolunteerPageReqModel reqModel);

    List<VolunteerEntity> save(VolunteerSaveReqModel reqModel);
    VolunteerEntity update(Long id, VolunteerSaveReqModel reqModel);
    void delete(Long id);
    String export(VolunteerPageReqModel reqModel);

    /**
     * 导入
     */
    Long importVolunteer(Long schoolId, String schoolYear, MultipartFile file);

    Map<Long, Integer> getVolunteerCount(Long classId,Long periodId);

    /**
     * 义工工作汇总-学生端(非鉴权)
     */
    List<VolunteerStudentSumResModel> sumByStudent(Long schoolId, String schoolYear, Long groupId);
}