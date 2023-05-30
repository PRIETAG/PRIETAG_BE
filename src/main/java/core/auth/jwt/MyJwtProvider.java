package core.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Date;

@Component @Slf4j
@RequiredArgsConstructor
public class MyJwtProvider {

    private static final String SUBJECT = "jwtstudy";
    private static final int EXP = 1000 * 60 * 60;
    public static final String TOKEN_PREFIX = "Bearer "; // 스페이스 필요함
    public static final String HEADER = "Authorization";
    private static final String SECRET = "메타코딩";

    public static String create(User user) {
        String jwt = JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP))
                .withClaim("id", user.getId())
                .withClaim("role", user.getRole().toString())
                .sign(Algorithm.HMAC512(SECRET));
        System.out.println("디버그 : 토큰 생성됨");
        return TOKEN_PREFIX + jwt;
    }

    public static DecodedJWT verify(String jwt) throws SignatureVerificationException, TokenExpiredException {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET))
                .build().verify(jwt);
        System.out.println("디버그 : 토큰 검증됨");
        return decodedJWT;
    }

//    private final AuthRepository authRepository;
//
//    private static final String SUBJECT = "jwtstudy";
//    private static final int EXP = 1000 * 60 * 60 * 24; // 24시간
//    public static final String TOKEN_PREFIX = "Bearer "; // 스페이스 필요함
//    public static final String HEADER = "Authorization";
//    private static String SECRET;
//
//    @Value("${jwt.secret}")
//    private void setSECRET(String secret) {
//        SECRET = secret;
//    }
//
//    @Transactional
//    public String create(User user) {
//        String accessToken = authRepository.findAuthByUserId(user.getId())
//                .map(Auth::getAccessToken)
//                .orElseGet(() -> {
//                        String newToken = JWT.create()
//                        .withSubject(SUBJECT)
//                        .withExpiresAt(new Date(System.currentTimeMillis() + EXP))
//                        .withClaim("id", user.getId())
//                        .withClaim("role", user.getRole().toString())
//                        .sign(Algorithm.HMAC512(SECRET));
//
//                        Auth auth = Auth.builder()
//                                .userId(user.getId())
//                                .accessToken(newToken)
//                                .build();
//                        authRepository.save(auth);
//
//                        return newToken;
//                    });
//
//        return TOKEN_PREFIX + accessToken;
//    }
//
//    public static String resolveToken(HttpServletRequest request) {
//        String jwt = request.getHeader(HEADER);
//        if (StringUtils.hasText(jwt) && jwt.startsWith(TOKEN_PREFIX)) return jwt.replaceAll(TOKEN_PREFIX,"");
//        return null;
//    }
//
//    public static boolean validateToken(String jwt) {
//        try {
//            Claims claims = Jwts.parser().setSigningKey(SECRET.getBytes()).parseClaimsJws(jwt).getBody();
//            return !claims.getExpiration().before(new Date());
//        } catch (JwtException | IllegalArgumentException e) {
//            return false;
//        }
//    }
//
//    @Transactional
//    public void invalidateToken(String jwt) {
//        //토큰 파싱
//        Claims claims = Jwts.parser().setSigningKey(SECRET.getBytes()).parseClaimsJws(jwt).getBody();
//        Long id = claims.get("id", Long.class);
//
//        //토큰 만료시간 설정
//        Date expirationDate = new Date(System.currentTimeMillis() - 1000);
//
//        //토큰 재발급
//        String accessToken = Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(new Date())
//                .setExpiration(expirationDate)
//                .signWith(SignatureAlgorithm.HS512, SECRET)
//                .compact();
//
//        Auth auth = authRepository.findAuthByUserId(id).orElseThrow(
//                () -> new Exception401("잘못된 토큰입니다")
//        );
//        auth.isRevokedAccessToken(accessToken);
//    }
//
//    @Transactional(readOnly = true)
//    public DecodedJWT verify(String jwt) throws SignatureVerificationException, TokenExpiredException {
//        Auth auth = authRepository.findAuthByAccessToken(jwt).orElseThrow(
//                () -> new TokenExpiredException("만료된 토큰입니다", Instant.MAX)
//        );
//        return JWT.require(Algorithm.HMAC512(SECRET))
//                .build().verify(auth.getAccessToken());
//    }
}