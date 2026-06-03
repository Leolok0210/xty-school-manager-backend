package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.model.entity.QualityEvaluationComment;
import com.xiaotiyun.school.manager.service.QualityEvaluationCommentService;
import com.xiaotiyun.school.manager.dao.QualityEvaluationCommentMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
* @author Akame
* @description 针对表【quality_evaluation_comment(素质登记评语设定表)】的数据库操作Service实现
* @createDate 2025-02-13 20:06:49
*/
@Service
public class QualityEvaluationCommentServiceImpl extends ServiceImpl<QualityEvaluationCommentMapper, QualityEvaluationComment>
    implements QualityEvaluationCommentService {

    @Override
    public List<QualityEvaluationComment> getByClassIdAndStudentIds(Long classId, List<Long> studentIds) {
        if(CollectionUtils.isEmpty(studentIds))
        {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<QualityEvaluationComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QualityEvaluationComment::getClassId, classId)
                .eq(QualityEvaluationComment::getDeleted, 0)
               .in(QualityEvaluationComment::getStudentId, studentIds);
        return list(wrapper);
    }
}