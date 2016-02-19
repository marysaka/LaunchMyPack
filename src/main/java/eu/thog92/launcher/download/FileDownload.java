package eu.thog92.launcher.download;

import java.io.*;
import java.net.URL;

public class FileDownload extends Downloadable
{

    private long filesize;

    public FileDownload(URL u, File f, long size)
    {
        super(u, f);
        this.filesize = size;
    }

    @Override
    public String download() throws IOException
    {
        boolean needupdate = false;
        String fileName = target.getName();
        if (!target.exists())
        {
            needupdate = true;

            try
            {
                target.getParentFile().mkdirs();
                target.createNewFile();

            } catch (IOException ex)
            {
                ex.printStackTrace();
                target.delete();
            }
        } else
        {
            long length = target.length();
            if (!(length == filesize))
            {
                needupdate = true;
            }
        }

        if (needupdate)
        {
            InputStream in = null;
            FileOutputStream fOut = null;
            try
            {
                in = url.openStream();
                fOut = new FileOutputStream(target);

                byte data[] = new byte[1024];
                int count;
                long totalBytesRead = 0;
                while ((count = in.read(data, 0, 1024)) != -1)
                {
                    fOut.write(data, 0, count);
                    totalBytesRead += count;
                    if (view != null)
                        view.setInfo("Downloading " + fileName + " (" + ((totalBytesRead / filesize) * 100) + " %)");

                }
            } catch (Exception e)
            {
                e.printStackTrace();
                // Launcher.getInstance().println("Cannot download file : " +
                // fileName, e);
                target.delete();
            } finally
            {
                this.close(in);
                this.close(fOut);
                // if(Launcher.getInstance().getStatut() != null)
                // {
                // Statut.setInfo("File " + fileName +
                // " downloaded successfully");
                // }

            }
            return "File " + fileName
                    + " downloaded successfully";
        }
        return "ERROR";

    }

    private void close(Closeable closeable)
    {
        if (closeable != null)
        {
            try
            {
                closeable.close();
            } catch (IOException e)
            {
                e.printStackTrace();
                target.delete();
            }
        }
    }

}
