package com.seventailed.engine.mappings;

import com.seventailed.engine.data.models.EngineRegisterUserModel;
import com.seventailed.engine.otp.EngineEmailConfirmationService;
import com.seventailed.engine.security.EngineUserService;
import com.seventailed.engine.utils.EngineUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EngineRegisterMapper {
    private Logger authenticationLogger = LoggerFactory.getLogger("authentication");

    @Autowired
    private EngineUserService engineUserService;

    @Autowired
    private EngineEmailConfirmationService engineEmailConfirmationService;

    @GetMapping("/register")
    public String register(HttpServletRequest request, HttpServletResponse response, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        if(!EngineUtils.isUserAuthenticated(authentication, false)) {
            if(engineEmailConfirmationService.hasOtp(request, response)) {
                redirectAttributes.addFlashAttribute("error", "Please validate your email address.");
                return "redirect:/confirmEmail";
            }

            model.addAttribute("registerUserModel", new EngineRegisterUserModel());
            return "register";
        }else {
            redirectAttributes.addFlashAttribute("error", "You are already logged in.");
            return "redirect:/";
        }
    }

    @PostMapping("/register")
    public String registerHandler(HttpServletRequest request, HttpServletResponse response, Model model, Authentication authentication, @ModelAttribute(name = "registerUserModel") EngineRegisterUserModel registerUserModel, RedirectAttributes redirectAttributes) {
        if(!EngineUtils.isUserAuthenticated(authentication, false)) {
            if(engineEmailConfirmationService.hasOtp(request, response)) {
                redirectAttributes.addFlashAttribute("error", "Please validate your email address.");
                return "redirect:/confirmEmail";
            }


            if(!EngineUtils.isValidName(registerUserModel.getFirstName())) {
                redirectAttributes.addFlashAttribute("error", "Invalid first name, please don't use numbers or characters that are not supported.");
                return "redirect:/register";
            }

            if(!EngineUtils.isValidName(registerUserModel.getLastName())) {
                redirectAttributes.addFlashAttribute("error", "Invalid last name, please don't use numbers or characters that are not supported.");
                return "redirect:/register";
            }

            if(!EngineUtils.isValidDate(registerUserModel.getBirthday())) {
                redirectAttributes.addFlashAttribute("error", "Invalid birthday.");
                return "redirect:/register";
            }

            if(engineUserService.getEngineUserRepository().existsByUsername(registerUserModel.getUsername())) {
                redirectAttributes.addFlashAttribute("error", "Username already exists.");
                return "redirect:/register";
            }

            if(engineUserService.getEngineUserRepository().existsByEmail(registerUserModel.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "Email already exists.");
                return "redirect:/register";
            }

            engineEmailConfirmationService.allocOtp(request, response, registerUserModel);
            return "redirect:/confirmEmail";
        }else {
            return "redirect:/";
        }
    }
}
