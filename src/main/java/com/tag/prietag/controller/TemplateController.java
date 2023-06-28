package com.tag.prietag.controller;

import com.tag.prietag.core.auth.session.MyUserDetails;
import com.tag.prietag.dto.ResponseDTO;
import com.tag.prietag.dto.template.TemplateRequest;
import com.tag.prietag.dto.template.TemplateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Template", description = "템플릿 API Document")
public class TemplateController {
    private final TemplateService templateService;

    @PostMapping(value = "/template", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "템플릿 생성", description = "템플릿을 새로 생성합니다")
    public ResponseEntity<?> createTemplate(@RequestPart @Valid TemplateRequest.SaveInDTO saveInDTO, Error errors,
                                            @RequestPart(value = "logoImageUrl", required = false) MultipartFile logoImage,
                                            @RequestPart(value = "previewUrl", required = false) MultipartFile previewImage,
                                            @AuthenticationPrincipal MyUserDetails myUserDetails) throws IOException {
        templateService.createTemplate( saveInDTO, myUserDetails.getUser(), logoImage, previewImage);
        return ResponseEntity.ok(new ResponseDTO<>());
    }

    @GetMapping("/templates")
    @Operation(summary = "템플릿 목록 조회", description = "유저의 모든 템플릿을 조회합니다")
    public ResponseEntity<?> getTemplates( @RequestParam(value = "page") int page,
                                           @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                           @AuthenticationPrincipal MyUserDetails myUserDetails){
        Pageable pageable = PageRequest.of(page, pageSize);
        TemplateResponse.getTemplatesOutDTO getTemplatesOutDTOList = templateService.getTemplates(myUserDetails.getUser(), pageable);
        return ResponseEntity.ok().body(new ResponseDTO<>(getTemplatesOutDTOList));
    }

    @GetMapping("/templates/{id}")
    @Operation(summary = "템플릿 버전(히스토리) 목록조회", description = "유저의 해당 템플릿의 모든 버전을 조회합니다")
    public ResponseEntity<?> getTemplatesVS(@PathVariable Long id,
                                            @RequestParam(value = "page") int page,
                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                            @RequestParam(value = "search", required = false, defaultValue = "") String search,
                                            @AuthenticationPrincipal MyUserDetails myUserDetails){
        Pageable pageable = PageRequest.of(page, pageSize);
        TemplateResponse.getTemplatesVSOutDTO getTemplatesVSOutDTOList = templateService.getTemplatesVS(id, pageable, search, myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>(getTemplatesVSOutDTOList));
    }


    // 템플릿 저장 -> 히스토리(버전) 생성 (모든 카드, 차트, FAQ 새로 생성)
    @PostMapping("/template/{templateId}")
    @Operation(summary = "템플릿 저장", description = "템플릿의 버전을 새로 생성합니다")
    public ResponseEntity<?> createTemplateVS(@PathVariable Long templateId,
                                              @RequestPart @Valid TemplateRequest.SaveInDTO saveInDTO, Error errors,
                                              @RequestPart(value = "logoImageUrl", required = false) MultipartFile logoImage,
                                              @RequestPart(value = "previewUrl", required = false) MultipartFile previewImage,
                                              @AuthenticationPrincipal MyUserDetails myUserDetails) throws IOException {
        String result = templateService.createTemplateVS(templateId, saveInDTO, myUserDetails.getUser(), logoImage, previewImage);
        return ResponseEntity.ok().body(new ResponseDTO<>(result));
    }


    // 템플릿 타이틀 수정
    @PatchMapping("/template/{templateId}/modify")
    @Operation(summary = "템플릿 타이틀 수정", description = "템플릿의 타이틀을 수정합니다")
    public ResponseEntity<?> updateTemplateName(@PathVariable Long templateId,
                                                @RequestBody TemplateRequest.UpdateInDTO updateInDTO, Error errors,
                                                @AuthenticationPrincipal MyUserDetails myUserDetails){
        String result = templateService.updateTemplateName(templateId, updateInDTO, myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>(result));
    }



    // 템플릿 복제 -> 최신 버전 1개만 복사
    // TODO: 새로운 템플릿 이름 받아야 함
    @PostMapping("/template/copy/{templateId}")
    @Operation(summary = "템플릿 복제", description = "템플릿의 최신버전만 포함해 복제합니다")
    public ResponseEntity<?> copyTemplate(@PathVariable Long templateId,
                                          @AuthenticationPrincipal MyUserDetails myUserDetails){
        String result = templateService.copyTemplate(templateId, myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>(result));
    }


    // 템플릿 퍼블리싱
    // 템플릿 아이디 -> 최신 버전
    // 버전 아이디 -> 해당 버전
    @PatchMapping("/template/publish")
    @Operation(summary = "템플릿 퍼블리싱", description = "해당 버전을 퍼블리싱합니다")
    public ResponseEntity<?> publishTemplate(@RequestParam(required = false, value = "tid") Long templateId,
                                             @RequestParam(required = false, value = "vid") Long versionId, Error errors,
                                             @AuthenticationPrincipal MyUserDetails myUserDetails){
        String result;
        if (templateId == null && versionId == null) {
            throw new IllegalArgumentException("templateId 또는 versionId 둘 중 하나는 필수입니다.");
        } else if (templateId != null && versionId != null) {
            throw new IllegalArgumentException("templateId 또는 versionId 둘 중 하나만 입력해주세요.");
        } else if (templateId != null) {
            result = templateService.publishTemplate(templateId, myUserDetails.getUser().getId());
        } else {
            result = templateService.publishTemplateVS(versionId, myUserDetails.getUser().getId());
        }
        return ResponseEntity.ok(new ResponseDTO<>(result));
    }


    // 템플릿 불러오기 (퍼블리싱)
    @GetMapping("/template/user/{userId}")
    @Operation(summary = "퍼블리싱된 템플릿 불러오기", description = "해당 유저의 퍼블리싱된 템플릿을 불러옵니다")
    public ResponseEntity<?> getTemplate(@PathVariable Long userId){
        TemplateResponse.TemplateVSOutDTO result = templateService.getPublishedTemplateVS(userId);
        return ResponseEntity.ok().body(new ResponseDTO<>(result));
    }

    // 템플릿 불러오기 (버전 선택)
    @GetMapping("/template/version/{versionId}")
    @Operation(summary = "템플릿 불러오기", description = "템플릿을 불러옵니다")
    public ResponseEntity<?> getTemplateVS(@PathVariable Long versionId,
                                           @AuthenticationPrincipal MyUserDetails myUserDetails){
        TemplateResponse.TemplateVSOutDTO result = templateService.getTemplateVS(versionId, myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>(result));
    }


    // 템플릿 히스토리(버전) 삭제 (여러개 선택 가능)
    @PatchMapping("/template/history")
    @Operation(summary = "템플릿 버전(히스토리) 삭제", description = "템플릿 버전(히스토리)들을 삭제합니다")
    public ResponseEntity<?> deleteTemplateVS(@RequestBody @Valid TemplateRequest.DeleteInDTO deleteInDTO,
                                              @AuthenticationPrincipal MyUserDetails myUserDetails){
        String result = templateService.deleteTemplateVS(deleteInDTO, myUserDetails.getUser());
        return ResponseEntity.ok(new ResponseDTO<>(result));
    }


    // 템플릿 삭제 (템플릿 및 포함 버전 전부 삭제)
    @PatchMapping("/template/{templateId}")
    @Operation(summary = "템플릿 삭제", description = "해당 템플릿과 버전들을 모두 삭제합니다")
    public ResponseEntity<?> deleteTemplate(@PathVariable Long templateId,
                                            @AuthenticationPrincipal MyUserDetails myUserDetails){
        String result = templateService.deleteTemplate(templateId, myUserDetails.getUser());
        return ResponseEntity.ok(new ResponseDTO<>(result));
    }

}
