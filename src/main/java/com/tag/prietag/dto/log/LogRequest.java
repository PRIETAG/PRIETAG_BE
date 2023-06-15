package com.tag.prietag.dto.log;

import com.tag.prietag.model.log.CustomerLog;
import lombok.Getter;

import javax.validation.constraints.NotNull;

public class LogRequest {

    @Getter
    public static class CustomerLogInDTO{
        @NotNull
        private CustomerLog.Type type;
        @NotNull
        private Long templateVersionId;
    }
}
