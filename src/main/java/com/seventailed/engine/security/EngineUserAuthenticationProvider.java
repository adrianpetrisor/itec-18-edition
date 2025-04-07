package com.seventailed.engine.security;

import com.seventailed.engine.email.EngineEmailService;
import com.seventailed.engine.utils.EngineUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class EngineUserAuthenticationProvider implements AuthenticationProvider {
    private Logger authenticationLogger = LoggerFactory.getLogger("authentication");

    @Autowired
    private EngineUserService engineUserService;

    @Autowired
    private EngineEmailService engineEmailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        if(username.isEmpty() || password.isEmpty()) {
            return null;
        }

        if(engineUserService.getEngineUserRepository().existsByUsername(username)) {
            if(engineUserService.validatePassword(username, password)) {
                EngineUserDetails engineUserDetails = new EngineUserDetails(engineUserService.getEngineUserRepository().findByUsername(username).get(), false);
                authenticationLogger.info("Authenticated user " + username + " with IP " + EngineUtils.getIP(request) + ".");

                engineUserService.getEngineUserIpsService().updateLastLoginForUser(engineUserDetails.getUser(), EngineUtils.getIP(request));
                engineEmailService.sendEmail("notification", engineUserDetails.getUser().getEmail(), "Account Security", "Login", "Hi " + engineUserDetails.getUser().getFirstName() + " " + engineUserDetails.getUser().getLastName() + ", someone logged in into your account with your username and password with this IP address: " + EngineUtils.getIP(request) + ". If this was you, please ignore this email.");

                if(engineUserDetails.getUser().getGoogleAuthKey() != null && !engineUserDetails.getUser().getGoogleAuthKey().isEmpty()) {
                    authenticationLogger.info("User " + engineUserDetails.getUser().getUsername() + " has google authenticator active, awaiting for code validation.");
                }

                return new UsernamePasswordAuthenticationToken(engineUserDetails, password, engineUserDetails.getAuthorities());
            }else {
                return null;
            }
        }else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
