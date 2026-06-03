package com.xiaotiyun.school.manager.basic.enums;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 职务枚举类
 */
@Getter
public enum JobTitleEnum {
    PRINCIPAL("1", "校長", "Principal", "Diretor"),
    VICE_PRINCIPAL("2", "副校長", "Vice Principal", "Vice-Diretor"),
    DIRECTOR("3", "主任", "Director", "Diretor"),
    ACADEMIC_DIRECTOR("4", "教務主任", "Academic Director", "Diretor Acadêmico"),
    TEACHING_DIRECTOR("5", "教導主任", "Teaching Director", "Diretor de Ensino"),
    MORAL_DIRECTOR("6", "德育主任", "Moral Education Director", "Diretor de Educação Moral"),
    GENERAL_AFFAIRS_DIRECTOR("7", "總務主任", "General Affairs Director", "Diretor de Assuntos Gerais"),
    DEPUTY_DIRECTOR("8", "副主任", "Deputy Director", "Diretor Adjunto"),
    DIRECTOR_ASSISTANT("9", "主任助理", "Director Assistant", "Assistente de Diretor"),
    SUBJECT_LEADER("10", "科組長", "Subject Leader", "Líder de Disciplina"),
    GRADE_LEADER("11", "級組長", "Grade Leader", "Líder de Série"),
    CLASS_TEACHER("12", "班主任", "Class Teacher", "Professor de Turma"),
    SUBJECT_TEACHER("13", "科任", "Subject Teacher", "Professor de Disciplina"),
    PROFESSIONAL_STAFF("14", "專職人員", "Professional Staff", "Funcionário Profissional"),
    CLERK("15", "文員", "Clerk", "Escriturário"),
    WORKER("16", "工友", "Worker", "Trabalhador");

    private final String code;
    private final String zhTwValue;
    private final String enValue;
    private final String ptValue;

    JobTitleEnum(String code, String zhTwValue, String enValue, String ptValue) {
        this.code = code;
        this.zhTwValue = zhTwValue;
        this.enValue = enValue;
        this.ptValue = ptValue;
    }

    /**
     * 根据编码和语言获取职务名称
     *
     * @param code     职务编码
     * @param language 语言
     * @return 职务名称
     */
    public static String getValue(String code, SchoolLanguageEnum language) {
        for (JobTitleEnum jobTitle : values()) {
            if (jobTitle.code.equals(code)) {
                switch (language) {
                    case EN_US:
                        return jobTitle.enValue;
                    case PT_PT:
                        return jobTitle.ptValue;
                    default:
                        return jobTitle.zhTwValue;
                }
            }
        }
        return "";
    }

    /**
     * 获取所有职务名称
     *
     * @param language 语言
     * @return 职务名称列表
     */
    public static List<String> allValues(SchoolLanguageEnum language) {
        List<String> list = new ArrayList<>();
        for (JobTitleEnum jobTitle : values()) {
            switch (language) {
                case EN_US:
                    list.add(jobTitle.enValue);
                    break;
                case PT_PT:
                    list.add(jobTitle.ptValue);
                    break;
                default:
                    list.add(jobTitle.zhTwValue);
                    break;
            }
        }
        return list;
    }

    /**
     * 根据职务名称和语言获取编码
     *
     * @param value    职务名称
     * @param language 语言
     * @return 职务编码
     */
    public static String getCode(String value, SchoolLanguageEnum language) {
        for (JobTitleEnum jobTitle : values()) {
            switch (language) {
                case EN_US:
                    if (jobTitle.enValue.equals(value))
                        return jobTitle.code;
                    break;
                case PT_PT:
                    if (jobTitle.ptValue.equals(value))
                        return jobTitle.code;
                    break;
                default:
                    if (jobTitle.zhTwValue.equals(value))
                        return jobTitle.code;
            }
        }
        return null;
    }

    /**
     * 根据职务，获取下级职务
     */
    public static List<String> getNextJobCodes(String value) {
        if (StringUtils.isEmpty(value)) return null;
        // 院长时返回 副院长
        if (value.equals(JobTitleEnum.PRINCIPAL.getCode())) {
            return Collections.singletonList(JobTitleEnum.VICE_PRINCIPAL.getCode());
        }
        // 副院长是返回 所有主任
        if (value.equals(JobTitleEnum.VICE_PRINCIPAL.getCode())) {
            return Arrays.asList(
                    JobTitleEnum.DIRECTOR.getCode(),
                    JobTitleEnum.ACADEMIC_DIRECTOR.getCode(),
                    JobTitleEnum.TEACHING_DIRECTOR.getCode(),
                    JobTitleEnum.MORAL_DIRECTOR.getCode(),
                    JobTitleEnum.GENERAL_AFFAIRS_DIRECTOR.getCode()
            );
        }
        // 任一主任是返回 其他除了 院长、副院长、主任外全部职务
        if (value.equals(JobTitleEnum.DIRECTOR.getCode()) ||
                value.equals(JobTitleEnum.GENERAL_AFFAIRS_DIRECTOR.getCode())){
            return Arrays.asList(
                    JobTitleEnum.DEPUTY_DIRECTOR.getCode(),
                    JobTitleEnum.DIRECTOR_ASSISTANT.getCode(),
                    JobTitleEnum.SUBJECT_LEADER.getCode(),
                    JobTitleEnum.GRADE_LEADER.getCode(),
                    JobTitleEnum.CLASS_TEACHER.getCode(),
                    JobTitleEnum.SUBJECT_TEACHER.getCode(),
                    JobTitleEnum.PROFESSIONAL_STAFF.getCode(),
                    JobTitleEnum.CLERK.getCode(),
                    JobTitleEnum.WORKER.getCode());
        }
        return null;
    }
}