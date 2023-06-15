package com.tag.prietag.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tag.prietag.dto.ResponseDTO;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyFilterResponseUtils {
    public static void unAuthorized(HttpServletResponse resp, Exception e) throws IOException {
        resp.setStatus(401);
        resp.setContentType("application/json; charset=utf-8");
        ResponseDTO<?> responseDto = new ResponseDTO<>(HttpStatus.UNAUTHORIZED, "unAuthorized", e.getMessage());
        ObjectMapper om = new ObjectMapper();
        String responseBody = om.writeValueAsString(responseDto);
        resp.getWriter().println(responseBody);
    }

    public static void forbidden(HttpServletResponse resp, Exception e) throws IOException {
        resp.setStatus(403);
        resp.setContentType("application/json; charset=utf-8");
        ResponseDTO<?> responseDto = new ResponseDTO<>(HttpStatus.FORBIDDEN, "forbidden", e.getMessage());
        ObjectMapper om = new ObjectMapper();
        String responseBody = om.writeValueAsString(responseDto);
        resp.getWriter().println(responseBody);
    }
}
