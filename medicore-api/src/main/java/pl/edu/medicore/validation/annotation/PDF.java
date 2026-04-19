package pl.edu.medicore.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pl.edu.medicore.validation.validator.PDFValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PDFValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PDF {
    String message() default "File must be a PDF";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
