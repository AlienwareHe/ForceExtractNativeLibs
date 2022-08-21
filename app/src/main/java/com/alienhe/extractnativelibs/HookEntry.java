package com.alienhe.extractnativelibs;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage {

    private static final String TAG = "ExtractNativeLibs";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("android")) {
            return;
        }
        Log.i(TAG, "ExtractNativeLibs Plugin Loaded!!!");
        try {
            if (Build.VERSION.SDK_INT >= 32) {
                XposedHelpers.findAndHookMethod("android.content.pm.parsing.ApkLite", lpparam.classLoader, "isExtractNativeLibs", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            String packageName = (String) XposedHelpers.getObjectField(param.thisObject, "mPackageName");
                            Log.i(TAG, packageName + " want to get isExtractNativeLibs:" + param.getResult());
                            if (!TextUtils.isEmpty(packageName) && packageName.equals("io.github.vvb2060.mahoshojo")) {
                                Log.i(TAG, "Force ExtractNativeLibs to true:" + packageName);
                                param.setResult(true);
                            }
                        } catch (Throwable e) {
                            Log.e(TAG, "hook isExtractNativeLibs error:", e);
                        }
                    }
                });
            } else {
                XposedBridge.hookAllConstructors(XposedHelpers.findClass("android.content.pm.PackageParser$ApkLite", lpparam.classLoader), new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            if (param.args.length < 18) {
                                return;
                            }
                            String packageName = (String) param.args[1];
                            boolean extractNativeLibs = Build.VERSION.SDK_INT > 30 ? (boolean) param.args[19]  :(boolean) param.args[18];
                            Log.i(TAG, packageName + " apkLite constructor params:" + extractNativeLibs);
                            if (!TextUtils.isEmpty(packageName) && packageName.equals("io.github.vvb2060.mahoshojo")) {
                                Log.i(TAG, "Force ExtractNativeLibs to true:" + packageName);
                                param.args[18] = true;
                            }
                        } catch (Throwable e) {
                            Log.e(TAG, "hook isExtractNativeLibs error:", e);
                        }
                    }
                });
            }

            Log.i(TAG, "ExtractNativeLibs Plugin Load Finish!!!");
        } catch (Throwable e) {
            Log.e(TAG, "hook android error:", e);
        }
    }
}
