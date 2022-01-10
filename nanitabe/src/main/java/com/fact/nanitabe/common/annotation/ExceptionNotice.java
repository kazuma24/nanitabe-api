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
 * 未定義のエラーが発生した場合のAOPアノテーション
 */
public @interface ExceptionNotice {

}
