/**
 * 
 */
package com.fact.nanitabe.common.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(METHOD)
/**
 * @author k-sato
 * JWTトークンの検証用フィルター
 */
public @interface JwtTokenCheck {

}
