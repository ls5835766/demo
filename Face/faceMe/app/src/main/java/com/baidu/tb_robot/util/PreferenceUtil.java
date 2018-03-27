package com.baidu.tb_robot.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.baidu.tb_robot.api.BDManager;

public class PreferenceUtil {

    private static SharedPreferences mSharedPreferences;

    private static synchronized SharedPreferences getPreferences() {
        if (mSharedPreferences == null) {
            // mSharedPreferences = App.context.getSharedPreferences(
            // PREFERENCE_NAME, Context.MODE_PRIVATE);
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(BDManager.Ext.getContext());
        }
        return mSharedPreferences;
    }

    /**
     * 打印所有
     */
    public static void print() {
        System.out.println(getPreferences().getAll());
    }

    /**
     * 清空保存在默认SharePreference下的所有数据
     */
    public static void clear() {
        getPreferences().edit().clear().apply();
    }

    /**
     * 保存字符串
     *
     * @return
     */
    public static void putString(String key, String value) {
        getPreferences().edit().putString(key, value).apply();
    }

    /**
     * 读取字符串
     *
     * @param key
     * @return
     */
    public static String getString(String key) {
        return getPreferences().getString(key, null);

    }

    /**
     * 保存整型值
     *
     * @return
     */
    public static void putInt(String key, int value) {
        getPreferences().edit().putInt(key, value).apply();
    }

    /**
     * 读取整型值
     *
     * @param key
     * @return
     */
    public static int getInt(String key) {
        return getPreferences().getInt(key, 0);
    }

    /**
     * 保存布尔值
     *
     * @return
     */
    public static void putBoolean(String key, Boolean value) {
        getPreferences().edit().putBoolean(key, value).apply();
    }

    public static void putLong(String key, long value) {
        getPreferences().edit().putLong(key, value).apply();
    }

    public static long getLong(String key) {
        return getPreferences().getLong(key, 0);
    }

    /**
     * t 读取布尔值
     *
     * @param key
     * @return
     */
    public static boolean getBoolean(String key, boolean defValue) {
        return getPreferences().getBoolean(key, defValue);

    }

    /**
     * 移除字段
     *
     * @return
     */
    public static void removeString(String key) {
        getPreferences().edit().remove(key).apply();
    }

}
