package com.tag.prietag.core.exception.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenException extends RuntimeException{

    TOKEN_ERROR tokenError;


    @Getter
    public enum TOKEN_ERROR{
        UNACCEPT(401,"Token is null or too short"), //토큰이 없거나 토큰 길이가 짧거나
        BADTYPE(401,"Token type Bearer"), //Bearer이 안 지켜졌을떄
        MALFORM(403,"Malformed Token"), //형태가 이상한 토큰
        BADSIGN(403,"BadSignatured Token"), //누군가 위조
        EXPIRED(403,"Expired Token"); // 만료

        private int status;
        private String msg;

        TOKEN_ERROR(int status,String msg){
            this.status = status;
            this.msg = msg;
        }
    }
    public TokenException(TOKEN_ERROR tokenError){
        super(tokenError.msg);
        this.tokenError = tokenError;
    }
    public void sendResponseError(HttpServletResponse response) {
        response.setStatus(tokenError.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ObjectMapper objectMapper = new ObjectMapper();

        CommonResponse<?> responseDto = new CommonResponse<>()
                .tokenError(tokenError)
                .data(false);

        try {
            String result = objectMapper.writeValueAsString(ErrorResponseDTO);
            response.getWriter().println(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}