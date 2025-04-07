package com.seventailed.engine.utils;

import com.seventailed.engine.entity.EngineUserEntity;
import com.seventailed.engine.security.EngineUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.swing.text.DateFormatter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class EngineUtils {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^(?=.{1,254}$)(?=.{1,64}@)[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+" +
                    "(\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*@" +
                    "([A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?\\.)+" +
                    "[A-Za-z]{2,}$"
    );
    private static final Logger log = LoggerFactory.getLogger(EngineUtils.class);

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDateTime getLocalTimeDateWithoutNanos() {
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.minusNanos(localDateTime.getNano());

        return localDateTime;
    }

    public static String getLocalTimeDate() {
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.minusNanos(localDateTime.getNano());

        return localDateTime.toString().replace("T", " ");
    }

    public static String getLocalTimeDate(LocalDateTime localDateTime) {
        localDateTime = localDateTime.minusNanos(localDateTime.getNano());

        return localDateTime.toString().replace("T", " ");
    }

    public static boolean isValidEmail(String email) {
        if(email != null) {
            if(EMAIL_PATTERN.matcher(email).matches()) {
                if(email.split("@")[1].contains("gmail.com")) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }

    public static boolean isValidFloat(String number) {
        try {
            Float.parseFloat(number);
            return true;
        }catch(Exception exception) {
            return false;
        }
    }

    public static boolean isAlphanumeric(String str) {
        return str != null && str.matches("^[a-zA-Z0-9]+$");
    }

    public static boolean isValidName(String str) {
        return str != null && str.matches("[\\p{L} ]+");
    }

    public static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, dateTimeFormatter);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    public static boolean isUserAuthenticated(Authentication authentication, boolean requiresGoogleAuthenticatorAuthorized) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof String && principal.equals("anonymousUser")) {
            return false;
        }

        if (principal instanceof UserDetails) {
            if(requiresGoogleAuthenticatorAuthorized) {
                EngineUserDetails engineUserDetails = (EngineUserDetails) principal;
                EngineUserEntity engineUserEntity = engineUserDetails.getUser();

                if(engineUserDetails.isOauth2Authorized()) {
                    return true;
                }

                if(engineUserEntity.getGoogleAuthKey() != null && !engineUserEntity.getGoogleAuthKey().isEmpty()) {
                    return engineUserDetails.isGoogleAuthenticationAuthorized();
                }else {
                    return true;
                }
            }else {
                return true;
            }
        }

        return false;
    }

    public static LocalDate parseDate(String date) {
        return LocalDate.parse(date, dateTimeFormatter);
    }

    public static String getIP(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        } else {
            ipAddress = ipAddress.split(",")[0];
        }

        return ipAddress;
    }

    public static boolean hasCookie(HttpServletRequest request, String name) {
        for (Cookie cookie : request.getCookies()) {
            if(cookie.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        for (Cookie cookie : request.getCookies()) {
            if(cookie.getName().equalsIgnoreCase(name)) {
                return cookie.getValue();
            }
        }

        return "";
    }
}
