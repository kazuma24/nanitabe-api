package com.fact.nanitabe.common.annotation;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import com.fact.nanitabe.common.constants.NanitabeMessageConstants;

/**
 * パスワード用バリデーションクラス
 * @author k-sato
 */
public class PasswordValidator implements ConstraintValidator<Password, String> {

	@Override
	public void initialize(Password password) {
	}

	/**
	 * 正常 true 異常 false
	 */
	@Override
	public boolean isValid(String input, ConstraintValidatorContext context) {
		if (StringUtils.isBlank(input)) {
			// null, "", " ", チェック
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(NanitabeMessageConstants.NEWREGISTER_MESSAGE_4).addConstraintViolation();
			return false;
		} else if (!Pattern.matches("^[0-9a-zA-Z]+$", input)) {
			// 半角英数字チェック
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(NanitabeMessageConstants.NEWREGISTER_MESSAGE_5).addConstraintViolation();
			return false;
		} else if (input.length() < 8 || 20 < input.length()) {
			//　文字数チェック
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(NanitabeMessageConstants.NEWREGISTER_MESSAGE_5).addConstraintViolation();
			return false;
		}
		return true;
	}

}
