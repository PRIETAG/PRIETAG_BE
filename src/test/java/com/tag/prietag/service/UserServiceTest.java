//package com.tag.prietag.service;
//
//import com.tag.prietag.core.util.Fetch;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.util.MultiValueMap;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class UserServiceTest {
//
//
//@DisplayName("엑세스 토큰 받아오기")
//@Test
//public void testAccessTokenReceiving() {
//    // 가상의 응답 데이터
//    String mockResponse = "{ \"access_token\": \"your-access-token\" }";
//
//    // Mock 객체 생성
//    ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
//    Fetch fetchMock = Mockito.mock(Fetch.class);
//
//    // Mock 객체를 사용하여 실제 호출 대신 가상의 응답 데이터 반환 설정
//    Mockito.when(fetchMock.kakao(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(MultiValueMap.class)))
//            .thenReturn(mockResponseEntity);
//
//    // 테스트할 메서드 호출
//    String code = "your-auth-code";
//    ResponseEntity<String> result = accessTokenReceiving(code, fetchMock);
//
//    // 예상되는 결과와 실제 결과 비교
//    assertEquals(HttpStatus.OK, result.getStatusCode());
//    assertEquals(mockResponse, result.getBody());
//}
//
//    public ResponseEntity<String> accessTokenReceiving(String code, Fetch fetch) {
//        // 기존 코드와 동일
//
//        ResponseEntity<String> codeEntity = fetch.kakao("https://kauth.kakao.com/oauth/token", HttpMethod.POST, body);
//
//        return codeEntity;
//    }
//}