package com.galapea.techblog.jobboardgriddbcloud.util;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Provide attributes available in all templates.
 */
@ControllerAdvice
public class WebAdvice {

    @ModelAttribute("requestUri")
    public String getRequestUri(final HttpServletRequest request) {
        return request.getRequestURI();
    }
}
