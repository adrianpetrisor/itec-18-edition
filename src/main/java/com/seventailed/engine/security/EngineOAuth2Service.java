package com.seventailed.engine.security;

import com.seventailed.engine.email.EngineEmailService;
import com.seventailed.engine.entity.EngineUserEntity;
import com.seventailed.engine.utils.EngineUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class EngineOAuth2Service extends DefaultOAuth2UserService {
    private Logger authenticationLogger = LoggerFactory.getLogger("authentication");

    @Autowired
    private EngineUserService engineUserService;

    @Autowired
    private EngineEmailService engineEmailService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        if(engineUserService.getEngineUserRepository().existsByEmail(email)) {
            EngineUserEntity engineUserEntity = engineUserService.getEngineUserRepository().findByEmail(email).get();
            engineUserService.getEngineUserIpsService().updateLastLoginForUser(engineUserEntity, EngineUtils.getIP(request));
            engineEmailService.sendEmail("notification", engineUserEntity.getEmail(), "Account Security", "Login with email", "Hi " + engineUserEntity.getFirstName() + " " + engineUserEntity.getLastName() + ", someone logged in into your account with your google address with this IP address: " + EngineUtils.getIP(request) + ". If this was you, please ignore this email.");
            authenticationLogger.info("OAuth2 authenticated user " + engineUserEntity.getUsername() + " with IP " + EngineUtils.getIP(request) + ".");
            return new EngineUserDetails(engineUserEntity, true);
        }else {
            return null;
        }
    }
}
