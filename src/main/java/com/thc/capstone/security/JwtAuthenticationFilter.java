package com.thc.capstone.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thc.capstone.dto.UserDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Transactional
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;
    private final AuthService authService;
    private final ExternalProperties externalProperties;

    /**
     *  로그인하려는 사용자의 자격을 확인해 토큰을 발급하는 함수.
     *  "/api/login" 으로 들어오는 요청에 실행된다.
     *  생성된 Authentication이 SecurityContextHolder에 등록되어 권한처리가 가능하게 한다.
     *
     *  @throws AuthenticationException
     */
    @Transactional
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Authentication authentication = null;
        UserDto.LoginReqDto userLoginDto = null;

        // 로그인에 필요한 아이디랑 비번이 있는지 확인
        try {
            userLoginDto = objectMapper.readValue(request.getInputStream(), UserDto.LoginReqDto.class);
        } catch (IOException e) {
            System.out.println("Login attemptAuthentication : Not Enough Parameters");
        }

        // 로그인에 필요한 아이디랑 비번으로 실제로 존재하는 고객인지 확인
        // 해당 정보로 Authentication 객체 생성
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userLoginDto.getUsername(), userLoginDto.getPassword());
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            System.out.println("Login attemptAuthentication : username, password not matched");
        }

        return authentication;
    }

    /**
     *  로그인 완료시 호출되는 함수.
     *  Refresh Token을 발급해 HttpServletRespons에 담는다.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        // UserId로 리프레시 토큰 발급
        String refreshToken = authService.createRefreshToken(principalDetails.getUser().getId());

        // Header에 담아서 전달
        response.addHeader(externalProperties.getRefreshKey(), externalProperties.getTokenPrefix() + refreshToken);

        System.out.println("Login Success");
    }
}
