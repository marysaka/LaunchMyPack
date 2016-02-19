package eu.thog92.launcher.util;

public class Constants
{

    private static String launcherName, libBase = "https://libraries.minecraft.net/", baseURL, launcherURL;

    public static void init(String name, String libraryPath, String basePath, String launcherPath)
    {
        launcherName = name;
        if (libraryPath != null)
            libBase = libraryPath;
        baseURL = basePath;
        if (launcherPath == null)
            launcherURL = basePath + "launcher.jar";
    }


    public static String getBaseURL()
    {
        return baseURL;
    }

    public static String getLauncherName()
    {
        return launcherName;
    }

    public static String getLibBase()
    {
        return libBase;
    }

    public static String getLauncherURL()
    {
        return launcherURL;
    }
}
