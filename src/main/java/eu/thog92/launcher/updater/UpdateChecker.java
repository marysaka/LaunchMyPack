package eu.thog92.launcher.updater;

import eu.thog92.launcher.download.FileDownload;
import eu.thog92.launcher.view.DownloadView;
import eu.thog92.launcher.util.Constants;
import eu.thog92.launcher.util.Util;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

public class UpdateChecker
{
    private final transient boolean isDevMod;
    private URL updateURL;

    public UpdateChecker(final boolean dm)
    {
        isDevMod = dm;
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
                updateURL = new URL(Constants.getLauncherURL());

                int filesize = Util.tryGetFileSize(updateURL);
                int filesizeclient = (int) new File(path).length();
                System.out.println(filesize + " - " + filesizeclient);
                if (filesize != filesizeclient)
                {
                    File temporaryUpdate = new File(temporaryUpdatePath);
                    temporaryUpdate.getParentFile().mkdir();

                    FileDownload downloadabletmp = new FileDownload(updateURL,
                            temporaryUpdate, filesize);
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    final DownloadView view = new DownloadView();
                    view.run();
                    view.setVisible(true);
                    downloadabletmp.setView(view);
                    try
                    {
                        downloadabletmp.download();
                        SelfUpdate.runUpdate(path, temporaryUpdatePath);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                }
            } catch (MalformedURLException e)
            {
                System.err.println(e.getMessage());
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            } catch (InstantiationException e)
            {
                e.printStackTrace();
            } catch (UnsupportedLookAndFeelException e)
            {
                e.printStackTrace();
            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }

        } else
        {
            System.out.println("Auto Update disabled (Dev Mode ?)");
        }
    }
}