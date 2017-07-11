package com.cf.takephotolibrary.utils;

import android.content.Context;

/**
 * 用于解决provider冲突的util
 */

public class ProviderUtil {

    public static String getFileProviderName(Context context){
        return context.getPackageName()+".fileprovider";
    }
}
