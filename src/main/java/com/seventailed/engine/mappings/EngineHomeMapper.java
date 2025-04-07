package com.seventailed.engine.mappings;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EngineHomeMapper {
    @GetMapping("/")
    public String home(HttpServletRequest httpServletRequest, Model model, Authentication authentication) {
        return "home";
    }
}
