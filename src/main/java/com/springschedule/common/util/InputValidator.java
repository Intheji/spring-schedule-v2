package com.springschedule.common.util;

public class InputValidator {

    // 안에 텍스트가 없는지 검사
    public static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " 비었습니다. 작성해 주세요.");
        }
    }

    // 최대 길이 제한 검사
    public static void requireMaxLength(String value, String fieldName, int maxLength) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + "비었습니다. 작성해 주세요");
        }
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " 최대 " + maxLength + "자까지 가능합니다");
        }
    }
}
