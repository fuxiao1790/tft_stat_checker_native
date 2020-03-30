package com.example.tft_stat_checker_native.Modal;

import android.content.Context;

import java.io.InputStream;
import java.util.Scanner;

public class JSONResourceReader {
    public static String readResource(int res, Context ctx){
        InputStream inputStream = ctx.getResources().openRawResource(res);
        return new Scanner(inputStream).useDelimiter("\\A").next();
    }
}
