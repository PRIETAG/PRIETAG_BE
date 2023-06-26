package com.tag.prietag.controller;

import com.tag.prietag.core.auth.session.MyUserDetails;
import com.tag.prietag.dto.ResponseDTO;
import com.tag.prietag.dto.template.TemplateRequest;
import com.tag.prietag.dto.template.TemplateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.tag.prietag.service.TemplateService;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class TemplateController {
    private final TemplateService templateService;

    @PostMapping(value = "/template", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createTemplate(@RequestPart @Valid TemplateRequest.SaveInDTO saveInDTO, Error errors,
                                            @RequestPart(value = "logoImageUrl") MultipartFile logoImage,
                                            @RequestPart(value = "previewUrl") MultipartFile previewImage,
                                            @AuthenticationPrincipal MyUserDetails myUserDetails) throws IOException {
        templateService.createTemplate( saveInDTO, myUserDetails.getUser(), logoImage, previewImage);
        return ResponseEntity.ok(new ResponseDTO<>());
    }

    @GetMapping("/templates")
    public ResponseEntity<?> getTemplates( @RequestParam(value = "page") int page,
                                           @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                           @AuthenticationPrincipal MyUserDetails myUserDetails){
        Pageable pageable = PageRequest.of(page, pageSize);
        TemplateResponse.getTemplatesOutDTO getTemplatesOutDTOList = templateService.getTemplates(myUserDetails.getUser(), pageable);
        return ResponseEntity.ok().body(new ResponseDTO<>(getTemplatesOutDTOList));
    }

    @GetMapping("/templates/{id}")
    public ResponseEntity<?> getTemplatesVS(@PathVariable Long id,
                                            @RequestParam(value = "page") int page,
                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                            @RequestParam(value = "search", defaultValue = "") String search,
                                            @AuthenticationPrincipal MyUserDetails myUserDetails){
        Pageable pageable = PageRequest.of(page, pageSize);
        TemplateResponse.getTemplatesVSOutDTO getTemplatesVSOutDTOList = templateService.getTemplatesVS(id, pageable, search, myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>(getTemplatesVSOutDTOList));
    }


    // 템플릿 저장 -> 히스토리(버전) 생성 (모든 카드, 차트, FAQ 새로 생성)
    @PostMapping("/template/{templateId}")
    public ResponseEntity<?> createTemplateVS(@PathVariable Long templateId,
                                              @RequestPart @Valid TemplateRequest.SaveInDTO saveInDTO, Error errors,
                                              @RequestPart(value = "logoImageUrl") MultipartFile logoImage,
                                              @RequestPart(value = "previewUrl") MultipartFile previewImage,
                                              @AuthenticationPrincipal MyUserDetails myUserDetails) throws IOException {
        String result = templateService.createTemplateVS(templateId, saveInDTO, myUserDetails.getUser(), logoImage, previewImage);
        return ResponseEntity.ok().body(new ResponseDTO<>(result));
    }


    // 템플릿 타이틀 수정
    @PatchMapping("/template/{templateId}/modify")
    public ResponseEntity<?> updateTemplateName(@PathVariable Long templateId,
                                                @RequestBody TemplateRequest.UpdateInDTO updateInDTO, Error errors,
                                                @AuthenticationPrincipal MyUserDetails myUserDetails){
        String result = templateService.updateTemplateName(templateId, updateInDTO, myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>(result));
    }



    // 템플릿 복제 -> 최신 버전 1개만 복사
    // TODO: 새로운 템플릿 이름 받아야 함
    @PostMapping("/template/copy/{templateId}")
    public ResponseEntity<?> copyTemplate(@PathVariable Long templateId,
                                          @AuthenticationPrincipal MyUserDetails myUserDetails){
        String result = templateService.copyTemplate(templateId, myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>(result));
    }


    // 템플릿 퍼블리싱
    // 템플릿 아이디 -> 최신 버전
    // 버전 아이디 -> 해당 버전
    @PatchMapping("/template/publish")
    public ResponseEntity<?> publishTemplate(@RequestParam(required = false, value = "tid") Long templateId,
                                             @RequestParam(required = false, value = "vid") Long versionId, Error errors,
                                             @AuthenticationPrincipal MyUserDetails myUserDetails){
        String result;
        if (templateId == null && versionId == null) {
            throw new IllegalArgumentException("templateId 또는 versionId 둘 중 하나는 필수입니다.");
        } else if (templateId != null && versionId != null) {
            throw new IllegalArgumentException("templateId 또는 versionId 둘 중 하나만 입력해주세요.");
        } else if (templateId != null) {
            result = templateService.publishTemplate(templateId, myUserDetails.getUser());
        } else {
            result = templateService.publishTemplateVS(versionId, myUserDetails.getUser());
        }
        return ResponseEntity.ok(new ResponseDTO<>(result));
    }


    // 템플릿 불러오기 (퍼블리싱)
    @GetMapping("/template/user/{userId}")
    public ResponseEntity<?> getTemplate(@PathVariable Long userId){
        TemplateResponse.TemplateVSOutDTO result = templateService.getPublishedTemplateVS(userId);
        return ResponseEntity.ok().body(new ResponseDTO<>(result));
    }

    // 템플릿 불러오기 (버전 선택)
    @GetMapping("/template/version/{versionId}")
    public ResponseEntity<?> getTemplateVS(@PathVariable Long versionId,
                                           @AuthenticationPrincipal MyUserDetails myUserDetails){
        TemplateResponse.TemplateVSOutDTO result = templateService.getTemplateVS(versionId, myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>(result));
    }


    // 템플릿 히스토리(버전) 삭제 (여러개 선택 가능)
    @PatchMapping("/template/history")
    public ResponseEntity<?> deleteTemplateVS(@RequestBody @Valid TemplateRequest.DeleteInDTO deleteInDTO,
                                              @AuthenticationPrincipal MyUserDetails myUserDetails){
        String result = templateService.deleteTemplateVS(deleteInDTO, myUserDetails.getUser());
        return ResponseEntity.ok(new ResponseDTO<>(result));
    }


    // 템플릿 삭제 (템플릿 및 포함 버전 전부 삭제)
    @PatchMapping("/template/{templateId}")
    public ResponseEntity<?> deleteTemplate(@PathVariable Long templateId,
                                            @AuthenticationPrincipal MyUserDetails myUserDetails){
        String result = templateService.deleteTemplate(templateId, myUserDetails.getUser());
        return ResponseEntity.ok(new ResponseDTO<>(result));
    }

}
