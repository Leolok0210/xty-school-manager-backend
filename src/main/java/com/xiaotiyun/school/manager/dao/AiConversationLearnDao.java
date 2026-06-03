package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.AiConversationLearnEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;
import java.util.List;

@Mapper
public interface AiConversationLearnDao extends BaseMapper<AiConversationLearnEntity> {

    @Select("SELECT * FROM ai_conversation_learn WHERE deleted = 0 AND normalized_query = #{normalizedQuery} AND school_id = #{schoolId} LIMIT 1")
    AiConversationLearnEntity findByNormalizedQuery(@Param("normalizedQuery") String normalizedQuery, @Param("schoolId") Long schoolId);

    @Select("SELECT * FROM ai_conversation_learn WHERE deleted = 0 AND is_learned = FALSE AND query_count >= 5 AND positive_count >= 3 ORDER BY positive_count DESC")
    List<AiConversationLearnEntity> findSuggestedFaqs();

    @Select("SELECT * FROM ai_conversation_learn WHERE deleted = 0 AND school_id = #{schoolId} ORDER BY query_count DESC LIMIT #{limit}")
    List<AiConversationLearnEntity> findTopQueries(@Param("schoolId") Long schoolId, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM ai_conversation_learn WHERE deleted = 0 AND school_id = #{schoolId}")
    int countBySchool(@Param("schoolId") Long schoolId);

    @Update("UPDATE ai_conversation_learn SET query_count = query_count + 1 WHERE id = #{id}")
    void incrementQueryCount(@Param("id") Long id);

    @Update("UPDATE ai_conversation_learn SET positive_count = positive_count + 1 WHERE id = #{id}")
    void incrementPositiveCount(@Param("id") Long id);

    @Update("UPDATE ai_conversation_learn SET negative_count = negative_count + 1 WHERE id = #{id}")
    void incrementNegativeCount(@Param("id") Long id);

    @Update("UPDATE ai_conversation_learn SET is_learned = TRUE WHERE id = #{id}")
    void markAsLearned(@Param("id") Long id);
}