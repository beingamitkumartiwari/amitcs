package com.amtee.camscanner.utilities;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

/**
 * Created by Deven on 18-04-2015.
 */
public class LvdTypeface {

    public static void overrideDefaultFont(Context context,String defaultFont,String customFont){
        try{
            final Typeface customFontTypeace= Typeface.createFromAsset(context.getAssets(), customFont);
            final Field defaultFontTypefaceField=Typeface.class.getDeclaredField(defaultFont);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null,customFontTypeace);
        }catch (Exception e){}
    }
}
