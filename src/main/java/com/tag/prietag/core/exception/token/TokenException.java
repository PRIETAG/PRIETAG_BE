package com.tag.prietag.core.exception.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tag.prietag.dto.ResponseDTO;
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

        private int status; // 상태 코드
        private String msg; // 메세지

        TOKEN_ERROR(int status,String msg){ // 생성자로 상태코드, 메세지 주입
            this.status = status;
            this.msg = msg;
        }
    }
    //생성자로 상태코드랑 메세지 주입 받음
    public TokenException(TOKEN_ERROR tokenError, HttpServletResponse response) {
        super(tokenError.msg);
        this.tokenError = tokenError;
        sendResponseError(response); // 여기서 예외를 json으로 클라이언트에게 전송함
    }
    public void sendResponseError(HttpServletResponse response) {
        response.setStatus(tokenError.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ObjectMapper objectMapper = new ObjectMapper();

        ResponseDTO<?> responseDto = new ResponseDTO<>();
        responseDto.setStatus(tokenError.getStatus());
        responseDto.setMsg(tokenError.getMsg());
        responseDto.setData(null);

        try {
            String result = objectMapper.writeValueAsString(responseDto);
            response.getWriter().println(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}