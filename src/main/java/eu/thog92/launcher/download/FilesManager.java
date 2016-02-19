package eu.thog92.launcher.download;

import eu.thog92.launcher.model.DownloadModel;
import eu.thog92.launcher.util.LogAgent;
import eu.thog92.launcher.util.Util;
import eu.thog92.launcher.view.IDownloadView;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class FilesManager implements Runnable
{

    private static final LogAgent LOG_AGENT = LogAgent.getLogAgent();
    private Map<String, String> serverIndex = new HashMap<String, String>();
    private Map<String, String> clientIndex = new HashMap<String, String>();
    private List<URL> urls = new ArrayList<URL>();
    private List<String> fileLines = new ArrayList<String>();
    private File indexFile;
    private IDownloadView view;
    private DownloadModel model;

    public FilesManager(DownloadModel m, IDownloadView v)
    {
        this.view = v;
        this.model = m;
        view.setStatut("Check for update ...");
        view.setIndeterminate(true);
    }

    @Override
    public void run()
    {
        indexFile = new File(model.getModPackDir(), "MD5SUMS");
        checkForUpdate();
        view.setIndeterminate(false);
    }

    private synchronized void checkForUpdate()
    {
        URL url1 = null;
        try
        {
            url1 = new URL(model.getDownloadURL() + "/MD5SUMS");
        } catch (MalformedURLException e1)
        {

            e1.printStackTrace();
        }
        HttpURLConnection yc = null;
        try
        {
            yc = (HttpURLConnection) url1.openConnection();
        } catch (IOException e1)
        {

            e1.printStackTrace();
        }

        BufferedReader in = null;
        try
        {
            in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream()));
        } catch (IOException e1)
        {

            e1.printStackTrace();
        }
        String inputLine;
        try
        {

            while ((inputLine = in.readLine()) != null)
            {
                fileLines.add(inputLine);
                String md5 = inputLine.substring(inputLine.lastIndexOf(' ') + 1);
                String name = inputLine.replace(" " + md5, "");
                serverIndex.put(name, md5);
            }

            in.close();
            // bw.close();
        } catch (IOException e1)
        {

            e1.printStackTrace();
        }

        if (!indexFile.exists())
        {
            this.createFile();
        }

        try
        {
            InputStream ips = new FileInputStream(indexFile);
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            String ligne;
            try
            {
                while ((ligne = br.readLine()) != null)
                {
                    String md5 = ligne.substring(ligne.lastIndexOf(' ') + 1);
                    String name = ligne.replace(" " + md5, "");

                    clientIndex.put(name, md5);
                }
            } catch (IOException e)
            {

                e.printStackTrace();
            }
        } catch (FileNotFoundException e)
        {

            e.printStackTrace();
        }
        checkRemote();
        checkLocal();

        if (!this.fileLines.isEmpty())
        {
            this.writeClientFilesLines();
        }
    }

    private void writeClientFilesLines()
    {
        // TODO Auto-generated method stub
        try
        {

            indexFile.createNewFile();
            FileOutputStream ops = new FileOutputStream(indexFile);
            OutputStreamWriter opsr = new OutputStreamWriter(ops);
            BufferedWriter bw = new BufferedWriter(opsr);
            for (String filesline : fileLines)
            {
                bw.write(filesline);
                bw.newLine();
                bw.flush();
            }
            bw.close();
            opsr.close();
            ops.close();

        } catch (IOException e)
        {

            e.printStackTrace();
        }
    }

    private void createFile()
    {
        try
        {

            indexFile.createNewFile();
            FileOutputStream ops = new FileOutputStream(indexFile);
            OutputStreamWriter opsr = new OutputStreamWriter(ops);
            BufferedWriter bw = new BufferedWriter(opsr);
            for (String filesline : fileLines)
            {

                bw.write(filesline);
                bw.newLine();
                bw.flush();

            }
            bw.close();

        } catch (IOException e)
        {

            e.printStackTrace();
        }
    }

    private void checkRemote()
    {
        LOG_AGENT.logInfo("Starting filesums checking ...");
        // TODO Auto-generated method stub
        Set<String> setserver = this.serverIndex.keySet();

        for (String name : setserver)
        {
            URL url = null;
            try
            {
                url = new URL(
                        model.getDownloadURL()
                                + name.replace("./", "/").replaceAll(" ", "%20"));
            } catch (MalformedURLException e)
            {

                e.printStackTrace();
            }
            String md5server = serverIndex.get(name);
            String md5client = clientIndex.get(name);

            File tmpfile = new File(model.getModPackDir(), name.replace("./", ""));
            long filesize = Util.tryGetFileSize(url);

            if (!tmpfile.exists())
            {
                this.addtoDownload(url, tmpfile, md5server, filesize);
            } else
            {
                if (md5client == null)
                {
                    if (!tmpfile.delete())
                    {
                        LOG_AGENT.logInfo("ERREUR SUR " + tmpfile.toString());
                    }
                    this.addtoDownload(url, tmpfile, md5server, filesize);
                }
            }
            view.setInfo(name.replace("./", ""));
            LOG_AGENT.logInfo("MD5 de " + name.replace("./", "") + " client:" + md5client + " , server:" + md5server);
        }
    }

    private void checkLocal()
    {
        // TODO Auto-generated method stub
        Set<String> setclient = this.clientIndex.keySet();
        for (String name : setclient)
        {
            URL url = null;
            try
            {
                url = new URL(
                        model.getDownloadURL()
                                + name.replace("./", "/").replaceAll(" ", "%20"));
            } catch (MalformedURLException e)
            {

                e.printStackTrace();
            }
            String md5server = serverIndex.get(name);
            String md5client = clientIndex.get(name);

            File tmpfile = new File(model.getModPackDir(), name.replace("./", ""));
            long filesize = Util.tryGetFileSize(url);
            boolean isDelete = false;
            if (tmpfile.getName().contains("minecraft.jar"))
            {
                //LOG_AGENT.logInfo("ICI");

            }
            if (tmpfile.getName().contains("minecraft.json"))
            {
                //LOG_AGENT.logInfo("ICI");

            }

            if (tmpfile.exists())
            {

                //LOG_AGENT.logInfo(tmpfile);
                //String md5file = FileHashSum.md5sum(tmpfile);
//                if(!md5file.equals(md5client))
//                {
//                  //LOG_AGENT.logInfo("md5 172");
//                  this.addtoDownload(url, tmpfile, md5server, filesize);
//                }
                if (md5server == null)
                {
                    LOG_AGENT.logInfo("File " + tmpfile.getAbsolutePath() + " is unused, removing...");

                    if (!tmpfile.delete())
                    {
                        LOG_AGENT.logInfo("ERREUR SUR " + tmpfile.toString());
                    }

                }
            }
            if (!isDelete)
            {
                if (md5server != null && md5client != null)
                {
                    if (!md5server.equals(md5client))
                    {
                        this.addtoDownload(url, tmpfile, md5server, filesize);
                    }
                }

            }

        }

    }

    private void addtoDownload(URL url, File tmpfile, String md5server, long filesize)
    {
        if (!urls.contains(url))
        {
            //LOG_AGENT.logInfo(url);
            model.addDownloadListModPack(new FileDownload(url, tmpfile, filesize));
            urls.add(url);
        }

    }
}
