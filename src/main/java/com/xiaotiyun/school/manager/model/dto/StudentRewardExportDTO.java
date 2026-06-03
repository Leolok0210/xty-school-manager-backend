package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class StudentRewardExportDTO {
    private String teacherName;
    private String teacherNumber;
    private String classNumber;
    private String reportTime;
    private String updateTime;
    private String studentNumber;
    private String studentName;
    private List<Record> records;
    private List<Formative> formative;
    private List<Summary> summary;
    private List<YearRecord> yearRecord;
    private List<Office> official;

    @Data
    public static class Record {
        //activityName
        private String activityName;
        private String careLevel;
        private String score;
        private String date;
        private String teacherName;
    }

    @Data
    public static class Formative {
        private String type;
        private String date;
        private String project;
        private String sum;
        private String remarks;
        private String typeOther;

    }
    @Data
    public static class Summary {
        private String date;
        private String project;
        private String remarks;
        private String records;
    }
    @Data
    public static class YearRecord {
        private String number;
        private String big;
        private String mini;
        private String defect;
        private String name;
        private String index;
    }

    @Data
    public static class Office {
        private String startTime;
        private String endTime;
        private String task;
    }
}
