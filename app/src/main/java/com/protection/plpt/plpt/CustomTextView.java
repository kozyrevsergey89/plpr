package com.protection.plpt.plpt;

/**
 * Created by sergey on 12/6/15.
 */
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;

/**
 * Military
 * <p/>
 * Created by Taras Sheremeta on 12/23/14
 * Copyright (c) 2014 Monster, Inc. All rights reserved
 */
public class CustomTextView extends TextView {

    public CustomTextView(Context context) {
        super(context);

        setupRobotoFont(null);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setupRobotoFont(attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setupRobotoFont(attrs);
    }

    private void setupRobotoFont(AttributeSet attrs) {
        FontType fontType = FontType.REGULAR;
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RobotoTextView);
            final int robotoFontValue = a.getInt(R.styleable.RobotoTextView_robotoFont, 0);

            FontType[] fontTypes = FontType.values();
            if (robotoFontValue >= 0 && robotoFontValue < fontTypes.length) {
                fontType = fontTypes[robotoFontValue];
            }

            a.recycle();
        }

        setTypeface(getRobotoTypeface(getContext(), fontType));
    }

    public enum FontType {
        REGULAR,
        MEDIUM,
        BOLD,
        LIGHT
    }

    /**
     * map of font types to font paths in assets
     */
    private static Map<FontType, String> fontMap = new HashMap<>();

    static {
        fontMap.put(FontType.REGULAR, "muller_extra_bold.ttf");
        fontMap.put(FontType.BOLD, "muller_extra_bold.ttf");
        fontMap.put(FontType.MEDIUM, "muller_extra_bold.ttf");
        fontMap.put(FontType.LIGHT, "muller_extra_bold.ttf");
    }

    /* cache for loaded Roboto typefaces*/
    private static Map<FontType, Typeface> typefaceCache = new HashMap<>();

    /**
     * Creates Roboto typeface and puts it into cache
     *
     * @param context - application context
     * @param fontType - font type name
     */
    public static Typeface getRobotoTypeface(Context context, FontType fontType) {
        String fontPath = fontMap.get(fontType);
        if (!typefaceCache.containsKey(fontType)) {
            typefaceCache.put(fontType, Typeface.createFromAsset(context.getAssets(), fontPath));
        }
        return typefaceCache.get(fontType);
    }
}
