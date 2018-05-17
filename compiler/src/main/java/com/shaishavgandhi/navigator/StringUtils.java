package com.shaishavgandhi.navigator;

import android.support.annotation.NonNull;

class StringUtils {

    static String capitalize(@NonNull String input) {
        if(input.length() == 0) {
            return "";
        } else if (input.length() == 1) {
            return input.toUpperCase();
        }

        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

}
