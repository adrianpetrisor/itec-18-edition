package com.seventailed.engine.security;

import com.seventailed.engine.data.models.EngineRegisterUserModel;
import com.seventailed.engine.data.repository.EngineEventMembersRepository;
import com.seventailed.engine.data.repository.EngineUserRepository;
import com.seventailed.engine.entity.EngineUserEntity;
import com.seventailed.engine.notifications.EngineNotificationsService;
import com.seventailed.engine.utils.EngineUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class EngineUserService {
    private Logger authenticationLogger = LoggerFactory.getLogger("authentication");

    @Autowired
    private EngineUserRepository engineUserRepository;

    @Autowired
    private EngineEventMembersRepository engineEventMembersRepository;

    @Autowired
    private EngineNotificationsService engineNotificationsService;

    @Autowired
    private EngineUserIpsService engineUserIpsService;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Bean
    public PasswordEncoder passwordEncoder() {
        return bCryptPasswordEncoder;
    }

    public void createUser(HttpServletRequest request, EngineRegisterUserModel engineRegisterUserModel) {
        if(!engineUserRepository.existsByUsername(engineRegisterUserModel.getUsername()) && !engineUserRepository.existsByEmail(engineRegisterUserModel.getEmail())) {
                EngineUserEntity engineUserEntity = new EngineUserEntity(engineRegisterUserModel.getUsername(), bCryptPasswordEncoder.encode(engineRegisterUserModel.getPassword()), engineRegisterUserModel.getEmail(), engineRegisterUserModel.getFirstName(), engineRegisterUserModel.getLastName(), EngineUtils.getLocalTimeDateWithoutNanos(), EngineUtils.getLocalTimeDateWithoutNanos(), LocalDate.parse(engineRegisterUserModel.getBirthday()), engineNotificationsService.createDefaultNotifications(), engineUserIpsService.createDefaultIps(EngineUtils.getIP(request)), "student");
            engineUserRepository.save(engineUserEntity);

            authenticationLogger.info("User " + engineRegisterUserModel.getUsername() + " with email " + engineRegisterUserModel.getEmail() + " and ip " + EngineUtils.getIP(request) + " was created.");
        }else {
            authenticationLogger.info("Attempted to create user for " + engineRegisterUserModel.getUsername() + " with email " + engineRegisterUserModel.getEmail() + " but already exists.");
        }
    }

    public boolean validatePassword(String username, String password) {
        Optional<String> foundPassword = engineUserRepository.findPasswordByUsername(username);
        if(foundPassword.isPresent()) {
            return bCryptPasswordEncoder.matches(password, foundPassword.get());
        }else {
            return false;
        }
    }

    public boolean isUserMemberOfEvent(EngineUserEntity engineUserEntity, String eventID) {
        return engineEventMembersRepository.isMemberOfEvent(engineUserEntity.getUniqueID(), eventID);
    }

    public EngineUserRepository getEngineUserRepository() {
        return engineUserRepository;
    }

    public EngineUserIpsService getEngineUserIpsService() {
        return engineUserIpsService;
    }
}
