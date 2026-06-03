package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.dao.ClassSeatDao;
import com.xiaotiyun.school.manager.model.entity.ClassSeat;
import com.xiaotiyun.school.manager.model.req.ClassSeatQueryReqModel;
import com.xiaotiyun.school.manager.model.res.ClassSeatDetailResModel;
import com.xiaotiyun.school.manager.service.ClassSeatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassSeatServiceImpl extends ServiceImpl<ClassSeatDao, ClassSeat> implements ClassSeatService {

    @Autowired
    private ClassSeatDao classSeatDao;

    @Override
    public void createClassSeats(List<ClassSeat> classSeats) {
        classSeats.forEach(classSeat -> {
            classSeat.setCreateTime(LocalDateTime.now());
            classSeat.setUpdateTime(LocalDateTime.now());
        });
        saveBatch(classSeats);
    }

    @Override
    public void updateClassSeat(ClassSeat classSeat) {
        classSeat.setUpdateTime(LocalDateTime.now());
        updateById(classSeat);
    }

    @Override
    public void deleteClassSeat(Long id) {
        //逻辑删除
        removeById(id);
    }

    @Override
    public ClassSeat getClassSeatById(Long id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    public PageInfo<ClassSeatDetailResModel> getClassSeatList(ClassSeatQueryReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        LambdaQueryWrapper<ClassSeat> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(reqModel.getSid()), ClassSeat::getSid, reqModel.getSid()) // 修改: != null 改为 StringUtils.isNotBlank
                .eq(reqModel.getClassId() != null, ClassSeat::getClassId, reqModel.getClassId())
                .eq(reqModel.getSeatNumber() != null, ClassSeat::getSeatNumber, reqModel.getSeatNumber())
                .eq(reqModel.getStudentId() != null, ClassSeat::getStudentId, reqModel.getStudentId())
                .eq(reqModel.getIsDeleted() != null, ClassSeat::getDeleted, reqModel.getIsDeleted());
        List<ClassSeat> classSeats = this.baseMapper.selectList(wrapper);
        List<ClassSeatDetailResModel> classSeatDetailResModels = classSeats.stream()
                .map(item -> {
                    ClassSeatDetailResModel resModel = new ClassSeatDetailResModel();
                    BeanUtils.copyProperties(item, resModel);
                    return resModel;
                }).collect(Collectors.toList());
        return new PageInfo<>(classSeatDetailResModels);
    }
}