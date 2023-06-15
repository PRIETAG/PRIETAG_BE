package com.tag.prietag.dto.template;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TemplateResponse {

    //Templates 조회
    @Getter
    public static class getTemplatesOutDTO{

        private Long id;
        private String title;
        private String updated_at;
        private String image;
        private boolean isPublished;

        @Builder
        public getTemplatesOutDTO(Long id, String title, ZonedDateTime updated_at, String image, boolean isPublished) {
            this.id = id;
            this.title = title;
            this.updated_at = updated_at.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
            this.image = image;
            this.isPublished = isPublished;
        }
    }

    @Getter
    public static class getTemplatesVSOutDTO{

        private Long id;
        private String title;
        private String updated_at;

        @Builder
        public getTemplatesVSOutDTO(Long id, String title, ZonedDateTime updated_at) {
            this.id = id;
            this.title = title;
            this.updated_at = updated_at.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
        }
    }
}
