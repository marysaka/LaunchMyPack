package eu.thog92.launcher.download;

import eu.thog92.launcher.util.IOUtils;
import eu.thog92.launcher.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ChecksummedDownloadable
        extends Downloadable
{
    private String checksum;

    public ChecksummedDownloadable(Proxy proxy, URL remoteFile, File localFile, boolean forceDownload)
    {
        super(proxy, remoteFile, localFile, forceDownload);
    }

    @Override
    public String download()
            throws IOException
    {
        this.numAttempts += 1;
        ensureFileWritable(getTarget());

        File target = getTarget();
        File checksumFile = new File(target.getAbsolutePath() + ".sha");
        String localHash = null;
        if (target.isFile())
        {
            localHash = getDigest(target, "SHA-1", 40);
        }
        if ((target.isFile()) && (checksumFile.isFile()))
        {
            this.checksum = readFile(checksumFile, "");
            if ((this.checksum.length() == 0) || (this.checksum.trim().equalsIgnoreCase(localHash)))
            {
                return "Local file matches local checksum, using that";
            }
            this.checksum = null;
            checksumFile.delete();
        }
        if (this.checksum == null)
        {
            try
            {
                HttpURLConnection connection = makeConnection(new URL(getUrl().toString() + ".sha1"));
                int status = connection.getResponseCode();
                if (status / 100 == 2)
                {
                    InputStream inputStream = connection.getInputStream();
                    try
                    {
                        this.checksum = StringUtils.toString(inputStream, "UTF-8");
                        StringUtils.writeStringToFile(checksumFile, this.checksum, "UTF-8");
                    } catch (IOException e)
                    {
                        this.checksum = "";
                    } finally
                    {
                        IOUtils.closeQuietly(inputStream);
                    }
                } else if (checksumFile.isFile())
                {
                    this.checksum = readFile(checksumFile, "");
                } else
                {
                    this.checksum = "";
                }
            } catch (IOException e)
            {
                if (target.isFile())
                {
                    this.checksum = readFile(checksumFile, "");
                } else
                {
                    throw e;
                }
            }
        }
        try
        {
            HttpURLConnection connection = makeConnection(getUrl());
            int status = connection.getResponseCode();
            if (status / 100 == 2)
            {
                updateExpectedSize(connection);

                InputStream inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(getTarget());
                String digest = copyAndDigest(inputStream, outputStream, "SHA", 40);
                if ((this.checksum == null) || (this.checksum.length() == 0))
                {
                    return "Didn't have checksum so assuming our copy is good";
                }
                if (this.checksum.trim().equalsIgnoreCase(digest))
                {
                    return "Downloaded successfully and checksum matched";
                }
                throw new RuntimeException(String.format("Checksum did not match downloaded file (Checksum was %s, downloaded %s)", new Object[]{this.checksum, digest}));
            }
            if (getTarget().isFile())
            {
                return "Couldn't connect to server (responded with " + status + ") but have local file, assuming it's good";
            }
            throw new RuntimeException(getTarget() + " - " + getUrl() +  ": Server responded with " + status);
        } catch (IOException e)
        {
            if ((getTarget().isFile()) && ((this.checksum == null) || (this.checksum.length() == 0)))
            {
                return "Couldn't connect to server (" + e.getClass().getSimpleName() + ": '" + e.getMessage() + "') but have local file, assuming it's good";
            }
            throw e;
        }
    }

    private String readFile(File file, String def)
    {
        try
        {
            return StringUtils.readFileToString(file, "UTF-8");
        } catch (Throwable ignored)
        {
        }
        return def;
    }
}
