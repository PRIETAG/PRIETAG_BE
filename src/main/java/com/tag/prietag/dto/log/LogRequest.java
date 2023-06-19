package com.tag.prietag.dto.log;

import com.tag.prietag.model.log.CustomerLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

public class LogRequest {

    @Getter @AllArgsConstructor
    @Builder
    public static class CustomerLogInDTO{
        @NotNull
        private CustomerLog.Type type;
        @NotNull
        private Long templateVersionId;
    }
}
