package com.xiaotiyun.school.manager.model.dto;

public class ImportErrorDTO {
    private Long taskId;
    private String incorrectReason;
    private int incorrectLineno;

    // Getters and Setters

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getIncorrectReason() {
        return incorrectReason;
    }

    public void setIncorrectReason(String incorrectReason) {
        this.incorrectReason = incorrectReason;
    }

    public int getIncorrectLineno() {
        return incorrectLineno;
    }

    public void setIncorrectLineno(int incorrectLineno) {
        this.incorrectLineno = incorrectLineno;
    }
}