package controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.TemplateService;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class TemplateController {
    TemplateService templateService;


}
