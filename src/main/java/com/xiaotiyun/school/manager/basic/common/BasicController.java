package com.xiaotiyun.school.manager.basic.common;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.model.entity.UserEntity;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 *
 */
public class BasicController {

	protected HttpServletRequest httpServletRequest() {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return requestAttributes.getRequest();
	}

	protected Long getSchoolId() {
		HttpServletRequest request = httpServletRequest();
		if (Objects.isNull(request.getHeader("schoolId"))) {
			throw new BusinessException(LanguageConstants.SCHOOL_ID_NOT_FOUND);
		}
		return Long.valueOf(request.getHeader("schoolId"));
	}

	protected long getSchoolId(HttpServletRequest request) {
		String schoolId = request.getHeader("schoolId");
		if (StringUtils.isEmpty(schoolId)) {
			throw new BusinessException(LanguageConstants.SCHOOL_ID_NOT_FOUND);
		}
		try {
			long result = Long.parseLong(schoolId);
			if(result<=0){
				throw new BusinessException(LanguageConstants.SCHOOL_ID_NOT_FOUND);
			}
			return result;
		} catch (NumberFormatException e) {
			throw new BusinessException(LanguageConstants.SCHOOL_ID_NOT_FOUND);
		}
	}

	protected Long getUserId() {
		UserEntity userInfo = (UserEntity) StpUtil.getSession().get("userInfo");
		if (userInfo == null) {
			throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
		}
		return userInfo.getId();
	}
}
