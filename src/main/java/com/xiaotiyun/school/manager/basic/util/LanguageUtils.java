package com.xiaotiyun.school.manager.basic.util;

import com.xiaotiyun.school.manager.basic.enums.DepartmentEnum;
import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import com.xiaotiyun.school.manager.basic.enums.StudentAttendanceImportTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.StudentImportIdTypeEnum;
import com.xiaotiyun.school.manager.model.res.MedicalRecordResModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LanguageUtils {
    //传入语言类型，判断传入是否是”是“，返回boolean
    public static boolean isYes(SchoolLanguageEnum languageEnum,String language) {
        switch (languageEnum){
            case ZH_MO:
                return "是".equals(language);
            case EN_US:
                return "Yes".equals(language);
            case PT_PT:
                return "Sim".equals(language);
        }
        return false;
    }
    //isNo
    public static boolean isNo(SchoolLanguageEnum languageEnum,String language) {
        switch (languageEnum){
            case ZH_MO:
                return "否".equals(language);
            case EN_US:
                return "No".equals(language);
            case PT_PT:
                return "Não".equals(language);
        }
        return false;
    }

    //判断是文科还是理科
    public static boolean getArtsScience(SchoolLanguageEnum languageEnum,String artsScience) {
        switch (languageEnum){
            case ZH_MO:
                return "文科".equals(artsScience) || "理工科".equals(artsScience);
            case EN_US:
                return "Arts".equals(artsScience) || "Engineering and Science".equals(artsScience);
            case PT_PT:
                return "Letras".equals(artsScience) || "Engenharia e Ciências".equals(artsScience);
        }
        return false;
    }
    //理科
    public static boolean getProfessional(SchoolLanguageEnum languageEnum,String artsScience) {
        switch (languageEnum){
            case ZH_MO:
                return "理科".equals(artsScience);
            case EN_US:
                return "Science".equals(artsScience);
            case PT_PT:
                return "Ciências".equals(artsScience);
        }
        return false;
    }

    //商科
    public static boolean getCommerce(SchoolLanguageEnum languageEnum,String artsScience) {
        switch (languageEnum){
            case ZH_MO:
                return "商科".equals(artsScience);
            case EN_US:
                return "Commerce".equals(artsScience);
            case PT_PT:
                return "Comercial".equals(artsScience);
        }
        return false;
    }

    //获取文理科
    public static String getArtsScience(SchoolLanguageEnum languageEnum, boolean artsScience) {
        switch (languageEnum){
            case ZH_MO:
                if(artsScience){
                    return "文科";
                }else {
                    return "理科";
                }
            case EN_US:
                if(artsScience){
                    return "Arts";
                }else {
                    return "Science";
                }
            case PT_PT:
                if(artsScience){
                    return "Letras";
                }else {
                    return "Ciências";
                }
            default:
                return "";
        }
    }

    //获取文理商科
    public static String getArtsScienceAndCommerce(SchoolLanguageEnum languageEnum, Integer artsScience) {
        switch (languageEnum){
            case ZH_MO:
                if(artsScience == 1){
                    return "文科";
                }else if (artsScience == 2){
                    return "理科";
                }else if (artsScience == 3){
                    return "商科";
                }
            case EN_US:
                if(artsScience == 1){
                    return "Arts";
                }else if (artsScience == 2){
                    return "Science";
                }else if(artsScience == 3)
                {
                    return "Commerce";
                }
            case PT_PT:
                if(artsScience == 1){
                    return "Letras";
                }else if (artsScience == 2){
                    return "Ciências";
                }else if(artsScience == 3)
                {
                    return "Comercial";
                }
            default:
                return "";
        }
    }

    //座位号
    public static String getSeatNumber(SchoolLanguageEnum languageEnum) {
        switch (languageEnum){
            case ZH_MO:
                return "座位號";
            case EN_US:
                return "Seat number";
            case PT_PT:
                return "Número do assento";
        }
        return "";
    }
    //姓名
    public static String getChineseName(SchoolLanguageEnum languageEnum) {
        switch (languageEnum){
            case ZH_MO:
                return "中文姓名";
            case EN_US:
                return "Chinese name";
            case PT_PT:
                return "Nome chinês";
        }
        return "";
    }

    public static String getStudentNumber(SchoolLanguageEnum languageEnum) {
        switch (languageEnum){
            case ZH_MO:
                return "學生編號";
            case EN_US:
                return "student number";
            case PT_PT:
                return "Número do aluno";
        }
        return "";
    }

    //获取StudentAttendanceImportTypeEnum
    public static StudentAttendanceImportTypeEnum getStudentAttendanceImportTypeEnum(SchoolLanguageEnum languageEnum,String type) {
        switch (languageEnum){
            case ZH_MO:
                if("入校".equals(type)){
                    return StudentAttendanceImportTypeEnum.IN;
                }else {
                    return StudentAttendanceImportTypeEnum.OUT;
                }
            case EN_US:
                if("Check-in".equals(type)){
                    return StudentAttendanceImportTypeEnum.IN;
                }else {
                    return StudentAttendanceImportTypeEnum.OUT;
                }
            case PT_PT:
                if("Entrada".equals(type)){
                    return StudentAttendanceImportTypeEnum.IN;
                }else {
                    return StudentAttendanceImportTypeEnum.OUT;
                }
        }
        return null;
    }
    //是否是男女
    public static boolean isMale(SchoolLanguageEnum languageEnum,String sex) {
        switch (languageEnum){
            case ZH_MO:
                return "男".equals(sex);
            case EN_US:
                return "Male".equals(sex);
            case PT_PT:
                return "Masculino".equals(sex);
        }
        return false;
    }
    //    ID_CARD(1, "身份证"),
    //    PASSPORT(2, "护照"),
    //    RE_ENTRY_PERMIT(3, "港澳居民来往内地通行证（回乡证）"),
    //    RESIDENCE_PERMIT(4, "港澳台居民居住证"),
    //    FOREIGNER_PERMIT(5, "外国人永久居留身份证（绿卡）");
    public static StudentImportIdTypeEnum getStudentImportIdTypeEnum(SchoolLanguageEnum languageEnum, String idType) {
        if(idType == null)
        {
            return null;
        }
        switch (languageEnum){
            case ZH_MO:
                if("身份證".equals(idType)){
                    return StudentImportIdTypeEnum.ID_CARD;
                }else if("護照".equals(idType)){
                    return StudentImportIdTypeEnum.PASSPORT;
                }else if("港澳台居民居住證".equals(idType)){
                    return StudentImportIdTypeEnum.RESIDENCE_PERMIT;
                }else if("外國人永久居留身份證（綠卡）".equals(idType)) {
                    return StudentImportIdTypeEnum.FOREIGNER_PERMIT;
                }
                break;
            case EN_US:
                if("Identity Card".equals(idType)){
                    return StudentImportIdTypeEnum.ID_CARD;
                }else if("Passport".equals(idType)){
                    return StudentImportIdTypeEnum.PASSPORT;
                }else if("Residence Permit for Hong Kong Macao and Taiwan Residents".equals(idType)){
                    return StudentImportIdTypeEnum.RESIDENCE_PERMIT;
                }else if("Green Card".equals(idType)) {
                    return StudentImportIdTypeEnum.FOREIGNER_PERMIT;
                }
            case PT_PT:
                if("Cartão de Identificação".equals(idType)){
                    return StudentImportIdTypeEnum.ID_CARD;
                }else if("Passaporte".equals(idType)){
                    return StudentImportIdTypeEnum.PASSPORT;
                }else if("Cartão de Residência para Residentes de Hong Kong Macau e Taiwan".equals(idType)){
                    return StudentImportIdTypeEnum.RESIDENCE_PERMIT;
                }else if("Cartão Verde".equals(idType)) {
                    return StudentImportIdTypeEnum.FOREIGNER_PERMIT;
                }
            default:
                return null;
        }
        return null;
    }

    //根据名称获取DepartmentEnum
    public static DepartmentEnum getDepartmentEnum(SchoolLanguageEnum languageEnum, String departmentName) {
        switch (languageEnum){
            case ZH_MO:
                switch (departmentName){
                    case "幼稚园":
                        return DepartmentEnum.KINDERGARTEN;
                    case "小学":
                        return DepartmentEnum.PRIMARY;
                    case "中学":
                        return DepartmentEnum.MIDDLE;
                }
                return null;
            case EN_US:
                switch (departmentName){
                    case "Kindergarten":
                        return DepartmentEnum.KINDERGARTEN;
                    case "Primary School":
                        return DepartmentEnum.PRIMARY;
                    case "Secondary School":
                        return DepartmentEnum.MIDDLE;
                }
                return null;
            case PT_PT:
                switch (departmentName){
                    case "Jardim de Infância":
                        return DepartmentEnum.KINDERGARTEN;
                    case "Escola Primária":
                        return DepartmentEnum.PRIMARY;
                    case "Escola Secundária":
                        return DepartmentEnum.MIDDLE;
                }
                return null;
            default:
                return null;
        }
    }

    //获取是否
    public static String getYes(SchoolLanguageEnum languageEnum,boolean language)
    {
        switch (languageEnum){
            case ZH_MO:
                if(language){
                    return "是";
                }else{
                    return "否";
                }
            case EN_US:
                if(language){
                    return "Yes";
                }else{
                    return "No";
                }
            case PT_PT:
                if(language){
                    return "Sim";
                }else{
                    return "Não";
                }
            default:
                return null;
        }
    }

    public static List<String> getAllDepartmentDesc(SchoolLanguageEnum languageEnum) {
        switch (languageEnum){
            case ZH_MO:
                return Arrays.asList("幼稚园", "小学", "中学");
            case EN_US:
                return Arrays.asList("Kindergarten", "Primary School", "Secondary School");
            case PT_PT:
                return Arrays.asList("Jardim de Infância", "Escola Primária", "Escola Secundária");
            default:
                return new ArrayList<>();
        }
    }

    public static String getChiefComplaint(SchoolLanguageEnum languageEnum, MedicalRecordResModel model) {
        StringBuilder sb = new StringBuilder();
        switch (languageEnum){
            case ZH_MO:
                if (model.isFever()) sb.append("發熱、");
                if (model.isCough()) sb.append("咳嗽、");
                if (model.isRunnyNose()) sb.append("流鼻血、");
                if (model.isSoreThroat()) sb.append("咽痛、");
                if (model.isDizziness()) sb.append("頭暈、");
                if (model.isHeadache()) sb.append("頭痛、");
                if (model.isNosebleed()) sb.append("流鼻血、");
                if (model.isNausea()) sb.append("惡心、");
                if (model.getVomitingCount() > 0) sb.append("嘔吐").append(model.getVomitingCount()).append("次、");
                if (model.isAbdominalPain()) sb.append("腹痛、");
                if (model.getDiarrheaCount() > 0) sb.append("腹泻").append(model.getDiarrheaCount()).append("次、");
                return sb.length() > 0 ? sb.deleteCharAt(sb.length() -1).toString() : null;
            case EN_US:
                if (model.isFever()) sb.append("fever、");
                if (model.isCough()) sb.append("cough、");
                if (model.isRunnyNose()) sb.append("nosebleed、");
                if (model.isSoreThroat()) sb.append("angina、");
                if (model.isDizziness()) sb.append("giddy、");
                if (model.isHeadache()) sb.append("headache、");
                if (model.isNosebleed()) sb.append("nosebleed、");
                if (model.isNausea()) sb.append("nausea、");
                if (model.getVomitingCount() > 0) sb.append("vomit").append(model.getVomitingCount()).append("time、");
                if (model.isAbdominalPain()) sb.append("celialgia、");
                if (model.getDiarrheaCount() > 0) sb.append("diarrhea").append(model.getDiarrheaCount()).append("time、");
                return sb.length() > 0 ? sb.deleteCharAt(sb.length() -1).toString() : null;
            case PT_PT:
                if (model.isFever()) sb.append("febre、");
                if (model.isCough()) sb.append("tosse、");
                if (model.isRunnyNose()) sb.append("Sangramento do nariz、");
                if (model.isSoreThroat()) sb.append("Dor de garganta、");
                if (model.isDizziness()) sb.append("Tonturas、");
                if (model.isHeadache()) sb.append("Dor de cabeça、");
                if (model.isNosebleed()) sb.append("Sangramento do nariz、");
                if (model.isNausea()) sb.append("Nojento、");
                if (model.getVomitingCount() > 0) sb.append("vómitos").append(model.getVomitingCount()).append("vez、");
                if (model.isAbdominalPain()) sb.append("Dor abdominal、");
                if (model.getDiarrheaCount() > 0) sb.append("diarreia").append(model.getDiarrheaCount()).append("vez、");
                return sb.length() > 0 ? sb.deleteCharAt(sb.length() -1).toString() : null;
            default:
                return null;
        }
    }
}
