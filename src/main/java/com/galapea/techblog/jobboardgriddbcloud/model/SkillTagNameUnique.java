package com.galapea.techblog.jobboardgriddbcloud.model;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import org.springframework.web.servlet.HandlerMapping;

import com.galapea.techblog.jobboardgriddbcloud.service.SkillTagGridDbService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Validate that the name value isn't taken yet.
 */
@Target({FIELD, METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = SkillTagNameUnique.SkillTagNameUniqueValidator.class)
public @interface SkillTagNameUnique {

    String message() default "{Exists.skillTag.name}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class SkillTagNameUniqueValidator implements ConstraintValidator<SkillTagNameUnique, String> {

        private final SkillTagGridDbService skillTagService;
        private final HttpServletRequest request;

        public SkillTagNameUniqueValidator(
                final SkillTagGridDbService skillTagService, final HttpServletRequest request) {
            this.skillTagService = skillTagService;
            this.request = request;
        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked")
            final Map<String, String> pathVariables =
                    ((Map<String, String>)
                            request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("id");
            if (currentId != null
                    && value.equalsIgnoreCase(skillTagService.get(currentId).getName())) {
                // value hasn't changed
                return true;
            }
            return !skillTagService.nameExists(value);
        }
    }
}
