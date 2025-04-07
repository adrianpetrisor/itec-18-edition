package com.seventailed.engine.security;

import com.seventailed.engine.email.EngineEmailService;
import com.seventailed.engine.entity.EngineUserEntity;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class EngineGoogleAuthenticatorService {
    private Logger accountLogger = LoggerFactory.getLogger("account");
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private EngineUserService engineUserService;

    @Autowired
    private EngineEmailService engineEmailService;

    private HashMap<String, Properties> awaitingKeys = new HashMap<>();

    protected GoogleAuthenticatorConfig googleAuthenticatorConfig = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().setWindowSize(6).build();
    private GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator(googleAuthenticatorConfig);

    private GoogleAuthenticatorKey generateSecretKey() {
        GoogleAuthenticatorKey googleAuthenticatorKey = googleAuthenticator.createCredentials();
        return googleAuthenticatorKey;
    }

    public String getQRCode(EngineUserEntity engineUserEntity) {
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL("Studentious", engineUserEntity.getEmail(), new GoogleAuthenticatorKey.Builder((String) awaitingKeys.get(engineUserEntity.getUsername()).get("key")).build());
    }

    public boolean verifyCode(String secretKey, int code) {
        return googleAuthenticator.authorize(secretKey, code);
    }

    public boolean verifyCode(EngineUserEntity engineUserEntity, int code) {
        return googleAuthenticator.authorize(engineUserEntity.getGoogleAuthKey(), code);
    }

    public boolean userHasKeyAwaiting(EngineUserEntity engineUserEntity) {
        return awaitingKeys.containsKey(engineUserEntity.getUsername());
    }

    public void generateKeyForUser(EngineUserEntity engineUserEntity) {
        if(!userHasKeyAwaiting(engineUserEntity)) {
            Properties properties = new Properties();

            GoogleAuthenticatorKey googleAuthenticatorKey = generateSecretKey();
            ScheduledFuture<?> scheduledFuture = executor.schedule(new Runnable() {
                @Override
                public void run() {
                    awaitingKeys.remove(engineUserEntity.getUsername());
                    accountLogger.info("Google authenticator key expired for " + engineUserEntity.getUsername() + ".");
                }
            },1, TimeUnit.MINUTES);

            properties.put("key", googleAuthenticatorKey.getKey());
            properties.put("future", scheduledFuture);

            awaitingKeys.put(engineUserEntity.getUsername(), properties);
            accountLogger.info("Generated google authenticator key for " + engineUserEntity.getUsername() + ", awaiting validation.");
        }
    }

    public boolean validateKeyForUserAndSaveIt(EngineUserEntity engineUserEntity, int code) {
        if(userHasKeyAwaiting(engineUserEntity)) {
            Properties properties = awaitingKeys.get(engineUserEntity.getUsername());
            String googleAuthenticatorKey = (String) properties.get("key");

            if(verifyCode(googleAuthenticatorKey, code)) {
                ScheduledFuture<?> future = (ScheduledFuture) properties.get("future");
                future.cancel(true);

                awaitingKeys.remove(engineUserEntity.getUsername());

                engineUserEntity.setGoogleAuthKey(googleAuthenticatorKey);
                engineUserService.getEngineUserRepository().updateGoogleAuthenticatorSecretKey(engineUserEntity.getUsername(), googleAuthenticatorKey);

                accountLogger.info("Google authenticator key validated for " + engineUserEntity.getUsername() + ".");
                engineEmailService.sendEmail("notification", engineUserEntity.getEmail(), "Google Authenticator", "Account Security", "Congratulations " + engineUserEntity.getFirstName() + " " + engineUserEntity.getLastName() + ", you secured your account with google authenticator.");

                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    public void deleteGoogleAuthenticatorKey(EngineUserEntity engineUserEntity) {
        engineUserEntity.setGoogleAuthKey(null);
        engineUserService.getEngineUserRepository().updateGoogleAuthenticatorSecretKey(engineUserEntity.getUsername(), null);

        engineEmailService.sendEmail("notification", engineUserEntity.getEmail(), "Google Authenticator", "Account Security", "Google authenticator was removed from your account.");
        accountLogger.info("Google authenticator key deleted for " + engineUserEntity.getUsername() + ".");
    }
}
