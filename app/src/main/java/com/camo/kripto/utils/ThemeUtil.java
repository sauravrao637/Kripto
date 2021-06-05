package com.camo.kripto.utils;
import com.camo.kripto.R;

import java.util.ArrayList;

/**
 * Created by Pankaj on 12-11-2017.
 */

public class ThemeUtil {
    public static final String THEME_RED = "THEME_RED";
    public static final String THEME_PINK = "THEME_PINK";
    public static final String THEME_PURPLE = "THEME_PURPLE";
    public static final String THEME_DEEPPURPLE = "THEME_DEEPPURPLE";
    public static final String THEME_INDIGO = "THEME_INDIGO";
    public static final String THEME_BLUE = "THEME_BLUE";
    public static final String THEME_LIGHTBLUE = "THEME_LIGHTBLUE";
    public static final String THEME_CYAN = "THEME_CYAN";
    public static final String THEME_TEAL = "THEME_TEAL";
    public static final String THEME_GREEN = "THEME_GREEN";
    public static final String THEME_LIGHTGREEN = "THEME_LIGHTGREEN";
    public static final String THEME_LIME = "THEME_LIME";
    public static final String THEME_YELLOW = "THEME_YELLOW";
    public static final String THEME_AMBER = "THEME_AMBER";
    public static final String THEME_ORANGE = "THEME_ORANGE";
    public static final String THEME_DEEPORANGE = "THEME_DEEPORANGE";
    public static final String THEME_BROWN = "THEME_BROWN";
    public static final String THEME_GRAY = "THEME_GRAY";
    public static final String THEME_BLUEGRAY = "THEME_BLUEGRAY";

    public static int getThemeId(String theme){
        int themeId;
        switch (theme){
            case THEME_PINK  :
                themeId = R.style.AppTheme_PINK;
                break;
            case THEME_PURPLE  :
                themeId = R.style.AppTheme_PURPLE;
                break;
            case THEME_DEEPPURPLE  :
                themeId = R.style.AppTheme_DEEPPURPLE;
                break;
            case THEME_INDIGO  :
                themeId = R.style.AppTheme_INDIGO;
                break;
            case THEME_BLUE  :
                themeId = R.style.AppTheme_BLUE;
                break;
            case THEME_LIGHTBLUE  :
                themeId = R.style.AppTheme_LIGHTBLUE;
                break;
            case THEME_CYAN  :
                themeId = R.style.AppTheme_CYAN;
                break;
            case THEME_TEAL  :
                themeId = R.style.AppTheme_TEAL;
                break;
            case THEME_GREEN  :
                themeId = R.style.AppTheme_GREEN;
                break;
            case THEME_LIGHTGREEN  :
                themeId = R.style.AppTheme_LIGHTGREEN;
                break;
            case THEME_LIME  :
                themeId = R.style.AppTheme_LIME;
                break;
            case THEME_YELLOW  :
                themeId = R.style.AppTheme_YELLOW;
                break;
            case THEME_AMBER  :
                themeId = R.style.AppTheme_AMBER;
                break;
            case THEME_ORANGE  :
                themeId = R.style.AppTheme_ORANGE;
                break;
            case THEME_DEEPORANGE  :
                themeId = R.style.AppTheme_DEEPORANGE;
                break;
            case THEME_BROWN  :
                themeId = R.style.AppTheme_BROWN;
                break;
            case THEME_GRAY  :
                themeId = R.style.AppTheme_GRAY;
                break;
            case THEME_BLUEGRAY  :
                themeId = R.style.AppTheme_BLUEGRAY;
                break;
            default:
                themeId = R.style.AppTheme_RED;
                break;
        }
        return themeId;
    }

    public static ArrayList<Theme> getThemeList(){
        ArrayList<Theme> themeArrayList = new ArrayList<>();
        themeArrayList.add(new Theme(0,R.color.primaryColorRed, R.color.primaryDarkColorRed, R.color.secondaryColorRed));
        themeArrayList.add(new Theme(1,R.color.primaryColorPink, R.color.primaryDarkColorPink, R.color.secondaryColorPink));
        themeArrayList.add(new Theme(2,R.color.primaryColorPurple, R.color.primaryDarkColorPurple, R.color.secondaryColorPurple));
        themeArrayList.add(new Theme(3,R.color.primaryColorDeepPurple, R.color.primaryDarkColorDeepPurple, R.color.secondaryColorDeepPurple));
        themeArrayList.add(new Theme(4,R.color.primaryColorIndigo, R.color.primaryDarkColorIndigo, R.color.secondaryColorIndigo));
        themeArrayList.add(new Theme(5,R.color.primaryColorBlue, R.color.primaryDarkColorBlue, R.color.secondaryColorBlue));
        themeArrayList.add(new Theme(6,R.color.primaryColorLightBlue, R.color.primaryDarkColorLightBlue, R.color.secondaryColorLightBlue));
        themeArrayList.add(new Theme(7,R.color.primaryColorCyan, R.color.primaryDarkColorCyan, R.color.secondaryColorCyan));
        themeArrayList.add(new Theme(8,R.color.primaryColorTeal, R.color.primaryDarkColorTeal, R.color.secondaryColorTeal));
        themeArrayList.add(new Theme(9,R.color.primaryColorGreen, R.color.primaryDarkColorGreen, R.color.secondaryColorGreen));
        themeArrayList.add(new Theme(10,R.color.primaryColorLightGreen, R.color.primaryDarkColorLightGreen, R.color.secondaryColorLightGreen));
        themeArrayList.add(new Theme(11,R.color.primaryColorLime, R.color.primaryDarkColorLime, R.color.secondaryColorLime));
        themeArrayList.add(new Theme(12,R.color.primaryColorYellow, R.color.primaryDarkColorYellow, R.color.secondaryColorYellow));
        themeArrayList.add(new Theme(13,R.color.primaryColorAmber, R.color.primaryDarkColorAmber, R.color.secondaryColorAmber));
        themeArrayList.add(new Theme(14,R.color.primaryColorOrange, R.color.primaryDarkColorOrange, R.color.secondaryColorOrange));
        themeArrayList.add(new Theme(15,R.color.primaryColorDeepOrange, R.color.primaryDarkColorDeepOrange, R.color.secondaryColorDeepOrange));
        themeArrayList.add(new Theme(16,R.color.primaryColorBrown, R.color.primaryDarkColorBrown, R.color.secondaryColorBrown));
        themeArrayList.add(new Theme(17,R.color.primaryColorGray, R.color.primaryDarkColorGray, R.color.secondaryColorGray));
        themeArrayList.add(new Theme(18,R.color.primaryColorBlueGray, R.color.primaryDarkColorBlueGray, R.color.secondaryColorBlueGray));
        return themeArrayList;
    }
}