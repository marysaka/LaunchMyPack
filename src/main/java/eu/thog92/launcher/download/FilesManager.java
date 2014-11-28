package eu.thog92.launcher.download;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import eu.thog92.launcher.demoimpl.model.DownloadModel;
import eu.thog92.launcher.demoimpl.view.DownloadIView;
import eu.thog92.launcher.util.LogAgent;

public class FilesManager implements Runnable {

    private Map<String, String> serverIndex = new HashMap<String, String>();
    private Map<String, String> clientIndex = new HashMap<String, String>();
    private List<URL> urls = new ArrayList<URL>();
    //private HashSet<Downloadable> dowloadable;
    private List<String> fileslines = new ArrayList<String>();
    private File indexFile;
    private DownloadIView view;

    private DownloadModel model;
    public FilesManager(DownloadModel m, DownloadIView v)
    {
        this.view = v;
        this.model = m;
        view.setStatut("Check for update ...");
        view.getProgressBar().setIndeterminate(true);
    }

    public void run() {
        // TODO Auto-generated method stub
        indexFile = new File(model.getModPackDir(), "MD5SUMS");
        //dowloadable =  new HashSet<Downloadable>();
        //log.logInfo(Thread.currentThread().getName());
        checkForUpdate();
        view.getProgressBar().setIndeterminate(false);
    }


    private synchronized void checkForUpdate() {
        //Statut.setStatut("Checking for update");

        URL url1 = null;
        try {
            url1 = new URL(model.getDownloadURL() + "/MD5SUMS");
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        HttpURLConnection yc = null;
        try {
            yc = (HttpURLConnection) url1.openConnection();
            System.out.println(yc.getResponseCode());
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream()));
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String inputLine;
        try {

            while ((inputLine = in.readLine()) != null) {
                fileslines.add(inputLine);
                String md5 = inputLine.substring(inputLine.lastIndexOf(' ') + 1);
                String name = inputLine.replace(" " + md5, "");
                serverIndex.put(name, md5);
            }

            in.close();
            // bw.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if (!indexFile.exists()) {
            this.createFile();
        }

        try {
            InputStream ips = new FileInputStream(indexFile);
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            String ligne;
            try {
                while ((ligne = br.readLine()) != null) {
                    String md5 = ligne.substring(ligne.lastIndexOf(' ') + 1);
                    String name = ligne.replace(" " + md5, "");

                    clientIndex.put(name, md5);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        checkRemote();

        checkLocal();
        if (!this.fileslines.isEmpty()) {
            this.writeClientFilesLines();
        }
//              job.addDownloadables(dowloadable);
//              Statut.addTask(job);
//              job.startDownloading();
//              

    }

    private void writeClientFilesLines() {
        // TODO Auto-generated method stub
        try {

            indexFile.createNewFile();
            FileOutputStream ops = new FileOutputStream(indexFile);
            OutputStreamWriter opsr = new OutputStreamWriter(ops);
            BufferedWriter bw = new BufferedWriter(opsr);
            for (int i = 0; i < fileslines.size(); i++) {

                bw.write(fileslines.get(i));
                bw.newLine();
                bw.flush();

            }
            bw.close();
            opsr.close();
            ops.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void createFile() {
        // TODO Auto-generated method stub
        try {

            indexFile.createNewFile();
            FileOutputStream ops = new FileOutputStream(indexFile);
            OutputStreamWriter opsr = new OutputStreamWriter(ops);
            BufferedWriter bw = new BufferedWriter(opsr);
            for (int i = 0; i < fileslines.size(); i++) {

                bw.write(fileslines.get(i));
                bw.newLine();
                bw.flush();

            }
            bw.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private static final LogAgent log = LogAgent.getLogAgent();

    private void checkRemote() {
        log.logInfo("Starting filesums checking ...");
        // TODO Auto-generated method stub
        Set<String> setserver = this.serverIndex.keySet();
        
        for (String name : setserver) {
            URL url = null;
            try {
                url = new URL(
                        model.getDownloadURL()
                        + name.replace("./", "/").replaceAll(" ", "%20"));
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String md5server = serverIndex.get(name);
            String md5client = clientIndex.get(name);

            //log.logInfo(name);
            File tmpfile = new File(model.getModPackDir(), name.replace("./", ""));
            long filesize = tryGetFileSize(url);
            
            if (!tmpfile.exists()) {
                this.addtoDownload(url, tmpfile, md5server, filesize);
            }
            else
            {
                if(md5client == null)
                {
                    if (!FileUtils.deleteQuietly(tmpfile.getAbsoluteFile())) {
                        log.logInfo("ERREUR SUR " + tmpfile.toString());
                    }
                    this.addtoDownload(url, tmpfile, md5server, filesize);
                }
            }
            view.setInfo(name.replace("./", ""));
            log.logInfo("MD5 de " + name.replace("./", "") + " client:" + md5client + " , server:" + md5server);
        }
    }

    private void checkLocal() {
        // TODO Auto-generated method stub
        Set<String> setclient = this.clientIndex.keySet();
        for (String name : setclient) {
            URL url = null;
            try {
                url = new URL(
                        model.getDownloadURL()
                        + name.replace("./", "/").replaceAll(" ", "%20"));
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String md5server = serverIndex.get(name);
            String md5client = clientIndex.get(name);

            File tmpfile = new File(model.getModPackDir(), name.replace("./", ""));
            long filesize = tryGetFileSize(url);
            boolean isDelete = false;
            if (tmpfile.getName().contains("minecraft.jar")) {
                //log.logInfo("ICI");
               
            }
            if (tmpfile.getName().contains("minecraft.json")) {
                //log.logInfo("ICI");
               
            }

            if (tmpfile.exists()) {

                //log.logInfo(tmpfile);
                //String md5file = FileHashSum.md5sum(tmpfile);
//                if(!md5file.equals(md5client))
//                {
//                  //log.logInfo("md5 172");
//                  this.addtoDownload(url, tmpfile, md5server, filesize);
//                }
                if (md5server == null) {
                    log.logInfo("File " + tmpfile.getAbsolutePath() + " is unused, removing...");

                    if (!FileUtils.deleteQuietly(tmpfile.getAbsoluteFile())) {
                        log.logInfo("ERREUR SUR " + tmpfile.toString());
                    }

                }
            }
            if (!isDelete) {
                if (md5server != null && md5client != null) {
                    if (!md5server.equals(md5client)) {
                        this.addtoDownload(url, tmpfile, md5server, filesize);
                    }
                }

            }

        }

    }

    private void addtoDownload(URL url, File tmpfile, String md5server, long filesize) {
        // TODO Auto-generated method stub
        //System.out.println(filesize);
        //log.logInfo(url);
        if (!urls.contains(url)) {
            System.out.println(tmpfile);
        //log.logInfo(url);
            //downloadabletmp.download();
            //dowloadable.add(downloadabletmp);
            model.addDownloadListModPack(new FileDownload(url, tmpfile, filesize));
            urls.add(url);
        }

    }

    private int tryGetFileSize(URL url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.getInputStream();
            return conn.getContentLength();
        } catch (IOException e) {
            return -1;
        } finally {
            conn.disconnect();
        }
    }
}
