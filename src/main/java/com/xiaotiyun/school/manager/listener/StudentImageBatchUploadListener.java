package com.xiaotiyun.school.manager.listener;

import com.xiaotiyun.school.manager.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(value = 2)
@RequiredArgsConstructor
public class StudentImageBatchUploadListener implements ApplicationRunner {
    private final StudentService studentService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("===================照片批量上传任务开始处理====================");
        studentService.queryUntreatedStudentImportTask();
        studentService.handleStudentImportBatchUpload();
    }
}
