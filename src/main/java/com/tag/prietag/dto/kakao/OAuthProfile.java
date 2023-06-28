package com.tag.prietag.dto.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Properties;

@Getter @Setter
@ToString
public class OAuthProfile {
    private Long id;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Properties properties;
    @JsonProperty("connected_at")
    private String connectedAt;
    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    public class KakaoAccount {
        @JsonProperty("has_email")
        private Boolean hasEmail;
        @JsonProperty("email_needs_agreement")
        private Boolean emailNeedsAgreement;
        @JsonProperty("is_email_valid")
        private Boolean isEmailValid;
        @JsonProperty("is_email_verified")
        private Boolean isEmailVerifed;
        @JsonProperty("profile_nickname_needs_agreement")
        private Boolean profile_nickname_needs_agreement;
        private String email;
    }
}
