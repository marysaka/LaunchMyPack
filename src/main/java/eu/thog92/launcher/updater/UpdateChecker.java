package eu.thog92.launcher.updater;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import org.apache.commons.io.FileUtils;

import eu.thog92.launcher.download.FileDownload;
import eu.thog92.launcher.util.Util;

public class UpdateChecker
{
    public static String verString = "";
    private static final String DOWNLOAD_ADRESS = "http://wherecraft.fr/Launcher/Wherecraft.jar";
    private boolean shouldUpdate;
    private URL updateURL;
    private final transient boolean isDevMod;
    
    public UpdateChecker(final boolean dm)
    {
        isDevMod = dm;
        loadInfo();
        try
        {
            FileUtils.deleteQuietly(new File(Util.getWorkingDirectory(), "updatetemp"));
        } catch (Exception ignored)
        {
            System.err.println(ignored.getMessage());
        }
    }
    
    private void loadInfo()
    {
        
    }
    
    public boolean shouldUpdate()
    {
        return shouldUpdate;
    }
    
    private int tryGetFileSize(URL url)
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
    
    public void update()
    {
        String path = null;
        try
        {
            path = new File(UpdateChecker.class.getProtectionDomain()
                    .getCodeSource().getLocation().getPath())
                    .getCanonicalPath();
            path = URLDecoder.decode(path, "UTF-8");
        } catch (IOException e)
        {
            System.out.println("Couldn't get path to current launcher jar/exe");
        }
        String temporaryUpdatePath = Util.getWorkingDirectory() + File.separator
                + "updatetemp" + File.separator
                + path.substring(path.lastIndexOf(File.separator) + 1);
        File pathfile = new File(path);
        System.out.println(pathfile);
        System.out.println(!isDevMod);
        if (pathfile.isFile() && !isDevMod)
        {
            try
            {
                updateURL = new URL(DOWNLOAD_ADRESS);
                
                int filesize = tryGetFileSize(updateURL);
                int filesizeclient = (int) new File(path).length();
                System.out.println(filesize + " - " + filesizeclient);
                if (filesize != filesizeclient)
                {
                    File temporaryUpdate = new File(temporaryUpdatePath);
                    temporaryUpdate.getParentFile().mkdir();
                    
                    FileDownload downloadabletmp = new FileDownload(updateURL,
                            temporaryUpdate, filesize);
                    try
                    {
                        downloadabletmp.download();
                        SelfUpdate.runUpdate(path, temporaryUpdatePath);
                    } catch (IOException e)
                    {
                        
                        e.printStackTrace();
                    }
                    // launcher.println("Server : "+filesize + " Client : " +
                    // filesizeclient );
                    
                }
                //
                // DownloadUtils.downloadToFile(updateURL, temporaryUpdate);
                
                // SelfUpdate.runUpdate(path, temporaryUpdatePath);
            } catch (MalformedURLException e)
            {
                System.err.println(e.getMessage());
            }
            
        } else
        {
            System.out.println("Auto Update disable (Dev Mode ?)");
        }
    }
}