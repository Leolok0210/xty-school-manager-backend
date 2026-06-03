package com.xiaotiyun.school.manager.handler;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.handler.context.SheetWriteHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.List;

@Slf4j
public class UserImportTemplateHeaderHandler implements SheetWriteHandler {
    private List<String> userGroups;
    private List<String> phoneAreas;
    private List<String> deptList;
    private List<String> genders;

    public UserImportTemplateHeaderHandler(List<String> userGroups, List<String> phoneAreas, List<String> positions, List<String> genders) {
        this.userGroups = userGroups;
        this.phoneAreas = phoneAreas;
        this.deptList = positions;
        this.genders = genders;
    }

    @Override
    public void afterSheetCreate(SheetWriteHandlerContext context) {
        if (CollectionUtils.isNotEmpty(phoneAreas)) {
            //设置手机号地区
            CellRangeAddressList phoneAreaList = new CellRangeAddressList(1, 10000, 1, 1);
            DataValidationHelper phoneAreaHelper = context.getWriteSheetHolder().getSheet().getDataValidationHelper();
            DataValidationConstraint phoneAreaConstraint = phoneAreaHelper.createExplicitListConstraint(phoneAreas.toArray(new String[0]));
            DataValidation phoneAreaDataValidation = phoneAreaHelper.createValidation(phoneAreaConstraint, phoneAreaList);
            phoneAreaDataValidation.setShowErrorBox(true);
            context.getWriteSheetHolder().getSheet().addValidationData(phoneAreaDataValidation);
        }
        if (CollectionUtils.isNotEmpty(deptList)) {
            //设置用户组
            CellRangeAddressList userGroupList = new CellRangeAddressList(1, 10000, 4, 4);
            DataValidationHelper userGroupHelper = context.getWriteSheetHolder().getSheet().getDataValidationHelper();
            DataValidationConstraint userGroupConstraint = userGroupHelper.createExplicitListConstraint(deptList.toArray(new String[0]));
            DataValidation userGroupDataValidation = userGroupHelper.createValidation(userGroupConstraint, userGroupList);
            userGroupDataValidation.setShowErrorBox(true);
            context.getWriteSheetHolder().getSheet().addValidationData(userGroupDataValidation);
        }
        if (CollectionUtils.isNotEmpty(userGroups)) {
            //设置用户组
            CellRangeAddressList userGroupList = new CellRangeAddressList(1, 10000, 5, 5);
            DataValidationHelper userGroupHelper = context.getWriteSheetHolder().getSheet().getDataValidationHelper();
            DataValidationConstraint userGroupConstraint = userGroupHelper.createExplicitListConstraint(userGroups.toArray(new String[0]));
            DataValidation userGroupDataValidation = userGroupHelper.createValidation(userGroupConstraint, userGroupList);
            userGroupDataValidation.setShowErrorBox(true);
            context.getWriteSheetHolder().getSheet().addValidationData(userGroupDataValidation);
        }
//        if (CollectionUtils.isNotEmpty(positions)) {
//            //设置职务
//            CellRangeAddressList positionList = new CellRangeAddressList(1, 10000, 6, 6);
//            DataValidationHelper positionHelper = context.getWriteSheetHolder().getSheet().getDataValidationHelper();
//            DataValidationConstraint positionConstraint = positionHelper.createExplicitListConstraint(positions.toArray(new String[0]));
//            DataValidation positionDataValidation = positionHelper.createValidation(positionConstraint, positionList);
//            positionDataValidation.setShowErrorBox(true);
//            context.getWriteSheetHolder().getSheet().addValidationData(positionDataValidation);
//        }
        if (CollectionUtils.isNotEmpty(genders)) {
            //设置性别
            CellRangeAddressList genderList = new CellRangeAddressList(1, 10000, 7, 7);
            DataValidationHelper genderHelper = context.getWriteSheetHolder().getSheet().getDataValidationHelper();
            DataValidationConstraint genderConstraint = genderHelper.createExplicitListConstraint(genders.toArray(new String[0]));
            DataValidation genderDataValidation = genderHelper.createValidation(genderConstraint, genderList);
            genderDataValidation.setShowErrorBox(true);
            context.getWriteSheetHolder().getSheet().addValidationData(genderDataValidation);
        }
    }
}
