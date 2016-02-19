package eu.thog92.launcher.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.CodeSource;

public class Path
{
    public static String getApplicationDirectory()
    {
        String jarDir = null;

        try
        {
            CodeSource codeSource = Path.class.getProtectionDomain().getCodeSource();
            File jarFile = new File(URLDecoder.decode(codeSource.getLocation().toURI().getPath(), "UTF-8"));
            jarDir = jarFile.getParentFile().getPath();
        } catch (URISyntaxException ex)
        {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
        }

        return jarDir + "/";
    }
}
