package eu.thog92.launcher.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;

/**
 * Desc...
 * Created by Thog the 19/02/2016
 */
public class StringUtils
{
    public static String join(List<String> list, String separator)
    {
        String result = "";
        for (String str : list)
        {
            result += str;
            result += separator;
        }
        return result;
    }

    public static String toString(InputStream in, String encoding) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(in, encoding));
        String read;

        while ((read = br.readLine()) != null)
            sb.append(read);

        br.close();
        return sb.toString();
    }

    public static void writeStringToFile(File file, String content, String value) throws IOException
    {
        Files.write(file.toPath(), Collections.singletonList(new String(content.getBytes(value), value)), Charset.forName(value), StandardOpenOption.CREATE);
    }

    public static String readFileToString(File file, String encoding) throws IOException
    {
        return new String(Files.readAllBytes(file.toPath()), encoding);
    }

    public static String readFileToString(File file, Charset encoding) throws IOException
    {
        return new String(Files.readAllBytes(file.toPath()), encoding);
    }
}
