package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.dto.ExternalCompetitionRecordDTO;
import com.xiaotiyun.school.manager.model.entity.ExternalCompetitionRecordEntity;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionQueryReqModel;
import com.xiaotiyun.school.manager.model.res.TranScriptGenerateResModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ExternalCompetitionRecordService extends IService<ExternalCompetitionRecordEntity> {
    /**
     * 获取参与比赛列表
     *
     * @param reqModel
     * @return
     */
    List<Long> partakeCompetitionList(ExternalCompetitionQueryReqModel reqModel);

    /**
     * 统计学生校外比赛登记记录
     *
     * @param semesterId 学期ID
     * @param classId    班级ID
     * @return 学生ID和登记的奖项记录数的映射
     */
    Map<Long, Integer> countCompetitionRecords(Long semesterId, Long classId);


    /**
     * 统计学生校外比赛登记记录
     *
     * @param semesterId 学期ID
     * @param classId    班级ID
     * @return 学生ID和登记的奖项记录数的映射
     */
    List<ExternalCompetitionRecordDTO> getCompetitionRecords(Long semesterId, Long classId);

    /**
     * 根据TranScriptGenerateResModel获取校外比赛记录
     *
     * @param result 成绩单生成结果列表
     */
    void getByTranScriptGenerateResModel(List<TranScriptGenerateResModel> result);
}