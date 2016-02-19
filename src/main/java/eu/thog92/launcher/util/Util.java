package eu.thog92.launcher.util;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Util
{
    public static final String APPLICATION_NAME = "minecraft";

    private static final String PATH = Path.getApplicationDirectory();

    public static OS getPlatform()
    {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win"))
        {
            return OS.WINDOWS;
        }
        if (osName.contains("mac"))
        {
            return OS.MACOS;
        }
        if (osName.contains("linux"))
        {
            return OS.LINUX;
        }
        if (osName.contains("unix"))
        {
            return OS.LINUX;
        }
        return OS.UNKNOWN;
    }

    public static File getWorkingDirectory()
    {
        return new File(PATH, Constants.getLauncherName()).getAbsoluteFile();
    }

    public static File getMinecraftDir()
    {
        String userHome = System.getProperty("user.home", ".");
        File workingDirectory;
        System.out.println(OperatingSystem.getCurrentPlatform().ordinal());
        switch (OperatingSystem.getCurrentPlatform().ordinal())
        {

            case 0:
                workingDirectory = new File(userHome, "." + "minecraft" + "/");
                break;
            case 1:
                String applicationData = System.getenv("APPDATA");
                String folder = applicationData != null ? applicationData : userHome;

                workingDirectory = new File(folder, "." + "minecraft" + "/");
                break;
            case 2:
                workingDirectory = new File(userHome, "Library/Application Support/" + "minecraft");
                break;
            default:
                workingDirectory = new File(userHome, "minecraft" + "/");
        }
        return workingDirectory;
    }

    public static int tryGetFileSize(URL url)
    {
        HttpURLConnection conn = null;
        try
        {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.getInputStream();
            return conn.getContentLength();
        } catch (IOException e)
        {
            return -1;
        } finally
        {
            conn.disconnect();
        }
    }

    public enum OS
    {
        WINDOWS, MACOS, SOLARIS, LINUX, UNKNOWN;

        OS()
        {
        }
    }
}
