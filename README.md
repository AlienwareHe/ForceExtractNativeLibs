This is a Xposed Module.

Force extractNativeLibs attr to true when install apk,because on the higher version of android, the default extractNativeLibs mode is false,and the ida can not find the so which we want to debug.