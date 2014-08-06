package fr.wherecraft.launcher.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import eu.thog92.launcher.controller.AbstractController;
import eu.thog92.launcher.download.Downloadable;
import eu.thog92.launcher.download.FileDownload;
import eu.thog92.launcher.download.FilesManager;
import eu.thog92.launcher.download.Job;
import eu.thog92.launcher.launch.GameLaunch;
import eu.thog92.launcher.util.Constants;
import eu.thog92.launcher.util.LogAgent;
import eu.thog92.launcher.util.OperatingSystem;
import eu.thog92.launcher.version.AssetIndex;
import eu.thog92.launcher.version.AssetObject;
import eu.thog92.launcher.version.Version;
import fr.wherecraft.launcher.model.DownloadModel;
import fr.wherecraft.launcher.model.LaunchModel;
import fr.wherecraft.launcher.model.RemotePack;
import fr.wherecraft.launcher.view.DownloadIView;
public class MainController extends AbstractController
{
    private static LaunchModel launchModel;
    private static DownloadModel downloadModel;
    private static RemotePack[] remote;
    public MainController()
    {
        launchModel = new LaunchModel();
        downloadModel = new DownloadModel();
        this.addModel(launchModel);
        this.addModel(downloadModel);
    }
    
    private static final LogAgent log = LogAgent.getLogAgent();
    public static final String USERNAME = "setUsername";
    public static final String DIR = "setDir";
    public static final String MC_JAR = "setJar";
    public static final String PACK = "setModPack";
    public static final String ELEMENT_BUTTON_PLAY = "playing";
    public static final String DOWNLOAD = "download";
    public boolean isOnline = false;
    
    public void preInit()
    {
        Socket sock = new Socket();
        InetSocketAddress addr = new InetSocketAddress("google.com",80);
        try {
            sock.connect(addr,3000);
            isOnline = true;
        } catch (IOException e) {
            isOnline = false;
        } finally {
            try {sock.close();}
            catch (IOException e) {}
        }
        
        if(isOnline)
        {
            File packjson = new File(Constants.LAUNCHER_NAME, "modpack.json");
            InputStream inputStream;
            try
            {
                URL indexUrl = new URL(Constants.BASE_DOWNLOAD_URL + "modpack.json");
                inputStream = indexUrl.openConnection(Proxy.NO_PROXY).getInputStream();
                FileUtils.writeStringToFile(packjson, IOUtils.toString(inputStream));
                
            } catch (IOException e)
            {
                if(!packjson.exists())
                {
                    JOptionPane.showMessageDialog(null, "No install found - Please connect to internet to play",
                            Constants.LAUNCHER_NAME + " -  Error",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }
            
            if(!packjson.exists())
            {
                JOptionPane.showMessageDialog(null, "No install found - Please connect to internet to play",
                        Constants.LAUNCHER_NAME + " -  Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            
            try
            {
                remote = new GsonBuilder().create().fromJson(FileUtils.readFileToString(packjson), RemotePack[].class);
            } catch (JsonSyntaxException e)
            {
                ;
            } catch (IOException e)
            {
                ;
            }
            
            
            
        }
    }
    
    public void pressPlayButton(String username)
    {
        if (StringUtils.isBlank(username))
            this.propertyChange(this, "DISPLAY_ERROR",
                    "Nom d'utilisateur obligatoire !");
        else
        {
            this.propertyChange(this, ELEMENT_BUTTON_PLAY, remote);
            this.setModelProperty(USERNAME, username);
            
        }
    }
    
    public void manageStart() throws InterruptedException
    {
        final DownloadIView view = new DownloadIView();
        view.run();
        view.setVisible(true);
        downloadModel.getModPackDir().mkdirs();
        Thread t = new Thread(new FilesManager(downloadModel, view), "CheckUpdate");
        t.start();
        t.join();
        
        System.out.println("FINISH CHECK");
        
        t = new Thread("ModPack")
        {
            
            @Override
            public void run()
            {
                Job modpack = new Job(downloadModel.getDownloadListModPack(), view);
                modpack.startDownload();
            }
        };
        t.start();
        t.join(); 
        System.out.println("FINISH MODPACK UPDATE");
        
        
        
        final Version v = Version.getVersion(new File(downloadModel.getModPackDir(), "bin/minecraft.json"));
        launchModel.setVersion(v);
        
        if(v != null)
        {
            view.setStatut("Download Requiere Lib ...");
            t = new Thread("Lib")
            {
                @Override
                public void run()
                {
                    Job modpack;
                    try
                    {
                        modpack = new Job(v.getRequiredDownloadables(OperatingSystem.getCurrentPlatform(), Proxy.NO_PROXY, downloadModel.getModPackDir().getParentFile(), false), view);
                        modpack.startDownload();
                    } catch (MalformedURLException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    
                }
            };
            t.start();
            t.join(); 
            System.out.println("FINISH LIB UPDATE");
            
            view.setStatut("Download Requiere Assets ...");
            t = new Thread("Assets")
            {
                
                @Override
                public void run()
                {
                    Job modpack = new Job(AssetIndex.getResourceFiles(v.getAssets(), downloadModel.getModPackDir().getParentFile()), view);
                    modpack.startDownload();
                }
            };
            t.start();
            t.join(); 
            System.out.println("FINISH ASSETS UPDATE");
            view.setStatut("Starting Minecraft ...");
            view.setInfo("Prepare the game before start");
            view.getProgressBar().setStringPainted(false);
            view.getProgressBar().setIndeterminate(true);
            
            GameLaunch launch = new GameLaunch(launchModel, downloadModel, log);
            try
            {
                this.propertyChange(this, "MASK", true);
                view.setVisible(false);
                launch.startGame();
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
           
        }
        

    
}

    public void selectModPack(RemotePack remotePack)
    {
        final File dir = new File(Constants.LAUNCHER_NAME);
        this.setModelProperty(PACK, new Object[] {remotePack, dir});
        this.propertyChange(this, PACK, null);
    }

}
