package com.cfo.common.captcha.enums;

/** 
 * 验证码的类型
 * @author Fu Yi
 *
 */
public enum CaptchaType {
	SIMPLE,
	COMPLEX,
	NUMBER;
	
    public static CaptchaType valueOf(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IndexOutOfBoundsException("Invalid ordinal");
        }
        return values()[ordinal];
    }
    
}
