package com.tag.prietag.dto.log;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

public class LogResponse {

    @Getter
    @AllArgsConstructor @Builder
    public static class GetTodayKpiOutDTO{

        private Kpi viewCount;
        private Kpi leaveCount;
        private Kpi conversionRate;

        @Getter
        @AllArgsConstructor @Builder
        public static class Kpi{
            Integer avgFromLast;
            Integer today;
        }
    }

    @Getter
    @AllArgsConstructor @Builder
    public static class GetTotalKpiOutDTO{

        private String label;
        private Integer viewCount;
        private Integer leaveCount;
        private Integer conversionRate;
    }

    @Getter
    @AllArgsConstructor @Builder
    public static class GetHistoryKpiOutDTO{

        private Long id;
        private String publishDate;
        private String versionName;
        private boolean isDeleted;
        private Integer viewCount;
        private Integer leaveCount;
        private Integer conversionRate;
    }
}
