package dto.log;

import lombok.Getter;
import model.Template;
import model.User;
import model.log.CustomerLog;

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
