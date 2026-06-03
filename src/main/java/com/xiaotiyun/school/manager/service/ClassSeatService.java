package com.xiaotiyun.school.manager.service;

import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.ClassSeat;
import com.xiaotiyun.school.manager.model.req.ClassSeatQueryReqModel;
import com.xiaotiyun.school.manager.model.res.ClassSeatDetailResModel;

import java.util.List;

public interface ClassSeatService {
    void createClassSeats(List<ClassSeat> classSeats);
    void updateClassSeat(ClassSeat classSeat);
    void deleteClassSeat(Long id);
    ClassSeat getClassSeatById(Long id);
    PageInfo<ClassSeatDetailResModel> getClassSeatList(ClassSeatQueryReqModel reqModel);
}