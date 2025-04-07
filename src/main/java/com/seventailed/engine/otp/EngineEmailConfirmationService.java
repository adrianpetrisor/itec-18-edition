package com.seventailed.engine.otp;

import com.seventailed.engine.data.models.EngineRegisterUserModel;
import com.seventailed.engine.email.EngineEmailService;
import com.seventailed.engine.security.EngineUserService;
import com.seventailed.engine.utils.EngineUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class EngineEmailConfirmationService {
    private Logger authenticationLogger = LoggerFactory.getLogger("Authentication");

    @Autowired
    private EngineUserService engineUserService;

    @Autowired
    private EngineEmailService engineEmailService;

    private Random random = new Random();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    private HashMap<String, Properties> otpCodes = new HashMap<>();

    public boolean allocOtp(HttpServletRequest request, HttpServletResponse response, EngineRegisterUserModel registerUserModel) {
        if(!hasOtp(request, response)) {
            Cookie otpCookie = new Cookie("OTP-IDENTIFIER", generateNewCookieIdentifier());
            int code = generateOTP();

            Properties otpProperties = new Properties();
            otpProperties.put("code", code);
            otpProperties.put("user", registerUserModel);

            ScheduledFuture<?> future = scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    otpCodes.remove(otpCookie.getValue());
                    authenticationLogger.info("OTP code for email " + registerUserModel.getEmail() + " has expired.");
                }
            }, 1, TimeUnit.MINUTES);

            otpProperties.put("future", future);
            otpCodes.put(otpCookie.getValue(), otpProperties);
            response.addCookie(otpCookie);

            engineEmailService.sendEmail("notification", registerUserModel.getEmail(), "Email confirmation", "Confirmation code", "Your email confirmation code is " + code + ".");
            authenticationLogger.info("Generated OTP code " + code + " for email " + registerUserModel.getEmail() + ".");

            return true;
        }else {
            return false;
        }
    }

    public boolean validateOtp(HttpServletRequest request, HttpServletResponse response, String otp) {
        if (otp == null) {
            return false;
        }

        String cookieValue = EngineUtils.getCookieValue(request, "OTP-IDENTIFIER");
        if(!EngineUtils.isNumeric(otp)) {
            return false;
        }

        if(hasOtp(request, response)) {
            Properties otpProperties = otpCodes.get(cookieValue);
            int code = Integer.parseInt(otp);
            if(code == (int) otpProperties.get("code")) {
                EngineRegisterUserModel engineRegisterUserModel = (EngineRegisterUserModel) otpProperties.get("user");
                engineUserService.createUser(request, engineRegisterUserModel);
                engineEmailService.sendEmail("notification", engineRegisterUserModel.getEmail(), "Account created", "Account", "Hi " + engineRegisterUserModel.getFirstName() + " " + engineRegisterUserModel.getLastName() + ", your account with username " + engineRegisterUserModel.getUsername() + " was created by IP " + EngineUtils.getIP(request) + ".");

                ScheduledFuture<?> scheduledFuture = (ScheduledFuture<?>) otpProperties.get("future");
                scheduledFuture.cancel(true);

                deleteOtpCookie(response);
                otpCodes.remove(cookieValue);

                authenticationLogger.info("OTP session for email " + engineRegisterUserModel.getEmail() + " was completed.");

                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    public boolean hasOtp(HttpServletRequest request, HttpServletResponse response) {
        if(!EngineUtils.hasCookie(request, "OTP-IDENTIFIER")) {
            return false;
        }

        String cookieValue = EngineUtils.getCookieValue(request, "OTP-IDENTIFIER");
        if(!otpCodes.containsKey(cookieValue)) {
            deleteOtpCookie(response);
            return false;
        }

        return true;
    }

    public String generateNewCookieIdentifier() {
        return UUID.randomUUID().toString();
    }

    public int generateOTP() {
        random.setSeed(System.currentTimeMillis());
        return random.nextInt(100000, 999999);
    }

    public void deleteOtpCookie(HttpServletResponse response) {
        Cookie deletedCookie = new Cookie("OTP-IDENTIFIER", null);
        deletedCookie.setMaxAge(0);
        deletedCookie.setPath("/");

        response.addCookie(deletedCookie);
    }
}
