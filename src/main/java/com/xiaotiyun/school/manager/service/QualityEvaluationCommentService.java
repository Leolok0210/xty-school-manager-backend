package com.xiaotiyun.school.manager.service;

import com.xiaotiyun.school.manager.model.entity.QualityEvaluationComment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Akame
* @description 针对表【quality_evaluation_comment(素质登记评语设定表)】的数据库操作Service
* @createDate 2025-02-13 20:06:49
*/
public interface QualityEvaluationCommentService extends IService<QualityEvaluationComment> {

    /**
     * 根据班级ID和批量学生ID查询数据
     * @param classId 班级ID
     * @param studentIds 学生ID列表
     * @return 评语列表
     */
    List<QualityEvaluationComment> getByClassIdAndStudentIds(Long classId, List<Long> studentIds);
}
