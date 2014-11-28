package eu.thog92.launcher.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public class FileDownload extends Downloadable
{
    
    private long filesize;
    
    public FileDownload(URL u, File f, long size)
    {
        super(u, f);
        this.filesize = size;
    }
    
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
            long lenght = FileUtils.sizeOf(target);
            if (!(lenght == filesize))
            {
                needupdate = true;
            }
        }
        
        if (needupdate)
        {
            InputStream in = null;
            FileOutputStream fout = null;
            try
            {
                in = url.openStream();
                fout = new FileOutputStream(target);
                
                byte data[] = new byte[1024];
                int count;
                long totalBytesRead = 0;
                while ((count = in.read(data, 0, 1024)) != -1)
                {
                    fout.write(data, 0, count);
                    totalBytesRead += count;
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
                if (in != null)
                    try
                    {
                        in.close();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                        target.delete();
                    }
                if (fout != null)
                    try
                    {
                        fout.close();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                        target.delete();
                    }
                // if(Launcher.getInstance().getStatut() != null)
                // {
                // Statut.setInfo("File " + fileName +
                // " downloaded successfully");
                // }
                
            }
            return"File " + fileName
                    + " downloaded successfully";
        }
        return "ERROR";
        
    }
    
}
