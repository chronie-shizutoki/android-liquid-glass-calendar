package com.example.androidcalendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import java.util.Locale;

public class LanguageManager {
    private static final String PREF_NAME = "language_pref";
    private static final String KEY_LANGUAGE = "selected_language";
    
    // 支持的语言代码
    public static final String LANGUAGE_ENGLISH = "en";
    public static final String LANGUAGE_CHINESE_SIMPLIFIED = "zh-CN";
    public static final String LANGUAGE_CHINESE_TRADITIONAL = "zh-TW";
    public static final String LANGUAGE_JAPANESE = "ja";
    
    private Context context;
    private SharedPreferences prefs;
    
    public LanguageManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * 设置应用语言
     */
    public void setLanguage(String languageCode) {
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply();
        updateLocale(languageCode);
    }
    
    /**
     * 获取当前设置的语言
     */
    public String getCurrentLanguage() {
        return prefs.getString(KEY_LANGUAGE, LANGUAGE_ENGLISH);
    }
    
    /**
     * 获取语言显示名称
     */
    public String getLanguageDisplayName(String languageCode) {
        switch (languageCode) {
            case LANGUAGE_ENGLISH:
                return "English";
            case LANGUAGE_CHINESE_SIMPLIFIED:
                return "简体中文";
            case LANGUAGE_CHINESE_TRADITIONAL:
                return "繁體中文";
            case LANGUAGE_JAPANESE:
                return "日本語";
            default:
                return "English";
        }
    }
    
    /**
     * 获取所有支持的语言
     */
    public String[] getSupportedLanguages() {
        return new String[]{
            LANGUAGE_ENGLISH,
            LANGUAGE_CHINESE_SIMPLIFIED,
            LANGUAGE_CHINESE_TRADITIONAL,
            LANGUAGE_JAPANESE
        };
    }
    
    /**
     * 获取所有语言的显示名称
     */
    public String[] getSupportedLanguageNames() {
        String[] languages = getSupportedLanguages();
        String[] names = new String[languages.length];
        for (int i = 0; i < languages.length; i++) {
            names[i] = getLanguageDisplayName(languages[i]);
        }
        return names;
    }
    
    /**
     * 更新应用的Locale
     */
    private void updateLocale(String languageCode) {
        Locale locale = createLocaleFromCode(languageCode);
        Locale.setDefault(locale);
        
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config);
        } else {
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }
    }
    
    /**
     * 从语言代码创建Locale对象
     */
    private Locale createLocaleFromCode(String languageCode) {
        switch (languageCode) {
            case LANGUAGE_CHINESE_SIMPLIFIED:
                return Locale.SIMPLIFIED_CHINESE;
            case LANGUAGE_CHINESE_TRADITIONAL:
                return Locale.TRADITIONAL_CHINESE;
            case LANGUAGE_JAPANESE:
                return Locale.JAPANESE;
            case LANGUAGE_ENGLISH:
            default:
                return Locale.ENGLISH;
        }
    }
    
    /**
     * 应用启动时初始化语言设置
     */
    public void initializeLanguage() {
        String savedLanguage = getCurrentLanguage();
        updateLocale(savedLanguage);
    }
    
    /**
     * 获取下一个语言（用于循环切换）
     */
    public String getNextLanguage() {
        String currentLang = getCurrentLanguage();
        String[] languages = getSupportedLanguages();
        
        for (int i = 0; i < languages.length; i++) {
            if (languages[i].equals(currentLang)) {
                return languages[(i + 1) % languages.length];
            }
        }
        
        return LANGUAGE_ENGLISH;
    }
}

