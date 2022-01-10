package com.fact.nanitabe.common.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
@Constraint(validatedBy = PasswordValidator.class)
@ReportAsSingleViolation
public @interface Password {

	String message() default "パスワードの値が不正です";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
}
