package com.maktoday.utils;

public class TextUtils {
    private TextUtils() {
    }

    public static boolean isCharacterOrSpaceOnly(String input) {
        for (char character : input.toCharArray()) {
            // Continue the loop is character is a letter or space
            if (Character.isLetter(character) || character == ' ') {
                continue;
            }

            // Return false if any of the character is something other than letter or space
            return false;
        }

        return true;
    }
}