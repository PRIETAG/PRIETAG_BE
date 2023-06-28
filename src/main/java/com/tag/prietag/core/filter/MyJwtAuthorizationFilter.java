package com.tag.prietag.core.filter;

import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.tag.prietag.core.auth.jwt.MyJwtProvider;
import com.tag.prietag.core.auth.session.MyUserDetails;
import com.tag.prietag.core.exception.token.TokenException;
import com.tag.prietag.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class MyJwtAuthorizationFilter extends BasicAuthenticationFilter {

    public MyJwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String prefixJwt = request.getHeader(MyJwtProvider.HEADER); //헤더에 토큰 없으면 다음 필터진행 밑에는 진행 X

        if (prefixJwt == null) {
            chain.doFilter(request, response);
            return;
        }

        String jwt = prefixJwt.replace(MyJwtProvider.TOKEN_PREFIX, "");
        try {
            System.out.println("디버그 : 토큰 있음");
            DecodedJWT decodedJWT = MyJwtProvider.verify(jwt);
            Long id = decodedJWT.getClaim("id").asLong();
            String role = decodedJWT.getClaim("role").asString();
            User.RoleEnum roleEnum = User.RoleEnum.fromString(role);

            User user = User.builder().id(id).role(roleEnum).build();
            MyUserDetails myUserDetails = new MyUserDetails(user);
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            myUserDetails,
                            myUserDetails.getPassword(),
                            myUserDetails.getAuthorities()
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("디버그 : 인증 객체 만들어짐");

        } catch (SignatureVerificationException sve) {
            log.error("토큰 검증 실패");
            throw new TokenException(TokenException.TOKEN_ERROR.EXPIRED, response);
        } catch (TokenExpiredException tee) { // 예외 처리 TokenException에서 대신 처리함
            log.error("토큰 만료됨");
            throw new TokenException(TokenException.TOKEN_ERROR.BADSIGN, response);
        } catch (InvalidClaimException ie) {
            log.error("유효하지 않은 토큰");
            throw new TokenException(TokenException.TOKEN_ERROR.MALFORM, response);
        } catch (JWTDecodeException jde){
            log.error("잘못된 형식의 토큰");
            throw new TokenException(TokenException.TOKEN_ERROR.MALFORM, response);
        } finally {
            chain.doFilter(request, response);
        }
    }
}
