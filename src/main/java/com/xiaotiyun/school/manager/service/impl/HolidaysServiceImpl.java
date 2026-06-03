package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.dao.HolidaysDao;
import com.xiaotiyun.school.manager.model.entity.HolidaysEntity;
import com.xiaotiyun.school.manager.service.HolidaysService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HolidaysServiceImpl extends ServiceImpl<HolidaysDao, HolidaysEntity> implements HolidaysService {

}