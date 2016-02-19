package eu.thog92.launcher.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Desc...
 * Created by Thog the 19/02/2016
 */
public class IOUtils
{
    public static void closeQuietly(InputStream inputStream)
    {
        if (inputStream != null)
        {
            try
            {
                inputStream.close();
            } catch (IOException ioe)
            {
                // ignored
            }
        }
    }
}
