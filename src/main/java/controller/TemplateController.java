package controller;

import core.auth.session.MyUserDetails;
import dto.ResponseDTO;
import dto.template.TemplateRequest;
import dto.template.TemplateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import service.TemplateService;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class TemplateController {
    private final TemplateService templateService;

    @PostMapping("/template")
    public ResponseEntity<?> createTemplate(@RequestBody @Valid TemplateRequest.SaveInDTO saveInDTO, Error errors, @AuthenticationPrincipal MyUserDetails myUserDetails){
        templateService.createTemplate( saveInDTO, myUserDetails.getUser());
        return ResponseEntity.ok(new ResponseDTO<>());
    }

    @GetMapping("/templates")
    public ResponseEntity<?> getTemplates( @RequestParam(value = "page") int page,
                                           @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                           @AuthenticationPrincipal MyUserDetails myUserDetails){
        Pageable pageable = PageRequest.of(page, pageSize);
        List<TemplateResponse.getTemplatesOutDTO> getTemplatesOutDTOList = templateService.getTemplates(myUserDetails.getUser(), pageable);
        return ResponseEntity.ok().body(new ResponseDTO<>(getTemplatesOutDTOList));
    }

    @GetMapping("/templates/{id}")
    public ResponseEntity<?> getTemplatesVS(@PathVariable Long id,
                                            @RequestParam(value = "page") int page,
                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                            @AuthenticationPrincipal MyUserDetails myUserDetails){
        Pageable pageable = PageRequest.of(page, pageSize);
        List<TemplateResponse.getTemplatesVSOutDTO> getTemplatesVSOutDTOList;
        return ResponseEntity.ok().body(new ResponseDTO<>(getTemplatesVSOutDTOList));
    }
}
