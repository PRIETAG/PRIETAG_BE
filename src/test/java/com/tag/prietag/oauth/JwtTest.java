package com.tag.prietag.oauth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class JwtTest {

    // ABC(메타코딩) -> 1313AB
    // ABC(시크) -> 5335KD

    // 1313AB 토큰
    @Test
    public void createJwt_test(){
        // given

        String myVar = "${jwt.secret}";
        // when
        String jwt = JWT.create()
                .withSubject("토큰제목")
                .withExpiresAt(new Date(System.currentTimeMillis()+1000*60*60))
                .withClaim("id", 1)
                .withClaim("role", "guest")
                .sign(Algorithm.HMAC512(myVar));
        System.out.println(jwt);
        // then
    }

    @Test
    public void verifyJwt_test(){
        // given

        String myVar = "adfsafddsf";


        String jwt = JWT.create()
                .withSubject("토큰제목")
                .withExpiresAt(new Date(System.currentTimeMillis()+1000*60*60*24*7))
                .withClaim("id", 1)
                .withClaim("role", "guest")
                .sign(Algorithm.HMAC512(myVar));
        System.out.println(jwt);

        // when
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(myVar))
                    .build().verify(jwt);
            int id = decodedJWT.getClaim("id").asInt();
            String role = decodedJWT.getClaim("role").asString();
            System.out.println(id);
            System.out.println(role);
        }catch (SignatureVerificationException sve){
            System.out.println("토큰 검증 실패 "+sve.getMessage()); // 위조
        }catch (TokenExpiredException tee){
            System.out.println("토큰 만료 "+tee.getMessage()); // 오래됨
        }


        // then
    }
}
