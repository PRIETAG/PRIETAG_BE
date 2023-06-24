package com.tag.prietag.controller;

import com.tag.prietag.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class healthCheckController {

    @GetMapping("/healthCheck")
    public ResponseEntity<?> getHealthCheck(){
        return ResponseEntity.ok(new ResponseDTO<>());
    }
}
