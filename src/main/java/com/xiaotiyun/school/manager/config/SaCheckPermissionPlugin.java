package com.xiaotiyun.school.manager.config;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;

import java.util.Optional;

@Component
public class SaCheckPermissionPlugin implements OperationBuilderPlugin {

    @Override
    public void apply(OperationContext context) {
        Optional<SaCheckPermission> annotation = context.findAnnotation(SaCheckPermission.class);
        if (annotation.isPresent()) {
            String[] permission = annotation.get().value();
            String description = context.operationBuilder().build().getNotes() == null ? "" : context.operationBuilder().build().getNotes();
            context.operationBuilder().notes(description + "\n所需权限: " + JSON.toJSONString(permission));
        }
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return DocumentationType.OAS_30.equals(documentationType);
    }
}

