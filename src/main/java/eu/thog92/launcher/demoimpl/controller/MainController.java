package eu.thog92.launcher.demoimpl.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Socket;
import java.net.URL;

import javax.swing.JOptionPane;

import net.kronos.mclib.auth.yggdrasil.AuthYggdrasil;
import net.kronos.mclib.auth.yggdrasil.AuthYggdrasilException;
import net.kronos.mclib.auth.yggdrasil.model.YggdrasilError;
import net.kronos.mclib.auth.yggdrasil.model.response.YggdrasilRefreshResponse;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import eu.thog92.launcher.controller.AbstractController;
import eu.thog92.launcher.demoimpl.model.DownloadModel;
import eu.thog92.launcher.demoimpl.model.LaunchModel;
import eu.thog92.launcher.demoimpl.model.RemotePack;
import eu.thog92.launcher.demoimpl.view.DownloadIView;
import eu.thog92.launcher.download.FilesManager;
import eu.thog92.launcher.download.Job;
import eu.thog92.launcher.launch.GameLaunch;
import eu.thog92.launcher.updater.UpdateChecker;
import eu.thog92.launcher.util.Constants;
import eu.thog92.launcher.util.LogAgent;
import eu.thog92.launcher.util.OperatingSystem;
import eu.thog92.launcher.util.Util;
import eu.thog92.launcher.version.AssetIndex;
import eu.thog92.launcher.version.Authentication;
import eu.thog92.launcher.version.MCLauncherProfiles;
import eu.thog92.launcher.version.Version;

public class MainController extends AbstractController {
    private static LaunchModel launchModel;
    private static DownloadModel downloadModel;
    private static RemotePack[] remote;

    public MainController() {
        downloadModel = new DownloadModel();
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
    private static MCLauncherProfiles authData;

    private AuthYggdrasil auth = new AuthYggdrasil(false);

    public void preInit() {
        Socket sock = new Socket();
        InetSocketAddress addr = new InetSocketAddress("google.com", 80);
        try {
            sock.connect(addr, 3000);
            isOnline = true;
        } catch (IOException e) {
            isOnline = false;
        } finally {
            try {
                sock.close();
            } catch (IOException e) {
            }
        }

        if (isOnline) {
            UpdateChecker updatecheck = new UpdateChecker(true);
            updatecheck.update();
            File packjson = new File(Util.getWorkingDirectory(), "modpack.json");
            InputStream inputStream;
            try {

                URL indexUrl = new URL(Constants.BASE_DOWNLOAD_URL
                        + "modpack.json");
                inputStream = indexUrl.openConnection(Proxy.NO_PROXY)
                        .getInputStream();
                FileUtils.writeStringToFile(packjson,
                        new String(IOUtils.toString(inputStream).getBytes(), "UTF-8"));

            } catch (IOException e) {
                e.printStackTrace();
                if (!packjson.exists()) {
                    JOptionPane
                            .showMessageDialog(
                                    null,
                                    "No install found - Please connect to internet to play",
                                    Constants.LAUNCHER_NAME + " -  Error",
                                    JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }

            if (!packjson.exists()) {
                JOptionPane
                        .showMessageDialog(
                                null,
                                "No install found - Please connect to internet to play",
                                Constants.LAUNCHER_NAME + " -  Error",
                                JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }

            File profile = new File(Util.getMinecraftDir(),
                    "launcher_profiles.json");

            if (!profile.exists()) {
                JOptionPane
                        .showMessageDialog(
                                null,
                                "Merci de lancer le launcher de Mojang et de vous identifiez avant de lancer ce launcher",
                                Constants.LAUNCHER_NAME + " -  Error",
                                JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }

            try {
                InputStream in = new FileInputStream(profile);
                InputStreamReader reader = new InputStreamReader(in);
                reader.close();
                authData = new GsonBuilder().create().fromJson(
                        FileUtils.readFileToString(profile, Charsets.UTF_8).replace(
                                "\uFEFF", ""), MCLauncherProfiles.class);
                remote = new GsonBuilder().create().fromJson(
                        FileUtils.readFileToString(packjson).replaceFirst("\\?", ""),
                        RemotePack[].class);

            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            String token = "0";
            String displayName = "Unknow";

            try {
                Authentication a = ((Authentication) authData
                        .getAuthenticationDatabase().get(
                                authData.getSelectedUser()));
                
                YggdrasilRefreshResponse rep = auth.refresh(a.getAccessToken(),
                        authData.getClientToken());
                displayName = a.getDisplayName();
                token = rep.getAccessToken();
                this.writeAuthData(profile, token);

            } catch (AuthYggdrasilException e) {
                YggdrasilError errorModel = e.getErrorModel();

                System.err.println(errorModel.getError());
                System.err.println(errorModel.getErrorMessage());
                System.err.println(errorModel.getCause());
                JOptionPane
                        .showMessageDialog(
                                null,
                                "You need to launch the Mojang launcher and log-in before starting this launcher",
                                Constants.LAUNCHER_NAME + " -  Error",
                                JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            } catch (IOException e) {

                e.printStackTrace();
            }

            File pref = new File(Util.getWorkingDirectory(), "preference.json");
            if (pref.exists()) {
                try {
                    launchModel = new GsonBuilder().create()
                            .fromJson(FileUtils.readFileToString(pref),
                                    LaunchModel.class);
                } catch (JsonSyntaxException e) {

                    e.printStackTrace();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }

            if (launchModel == null)
                launchModel = new LaunchModel();
            launchModel.setUsername(displayName);
            launchModel.setToken(token);
            this.addModel(launchModel);

        }
    }

    private void writeAuthData(File file, String string) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        StringBuffer buffer = new StringBuffer();
        while ((line = br.readLine()) != null) {
            if (line.contains("accessToken")) {
                line = "      \"accessToken\": \"" + string + "\",";
            }
            buffer.append(line);
            buffer.append("\n");
        }
        br.close();

        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(buffer.toString());
        bw.close();
    }

    public void pressPlayButton() {
        this.propertyChange(this, ELEMENT_BUTTON_PLAY, remote);
        this.savePreference();
    }

    public void manageStart() throws InterruptedException {
        final DownloadIView view = new DownloadIView();
        view.run();
        view.setVisible(true);
        downloadModel.getModPackDir().mkdirs();
        Thread t = new Thread(new FilesManager(downloadModel, view),
                "CheckUpdate");
        t.start();
        t.join();

        System.out.println("FINISH CHECK");

        t = new Thread("ModPack") {

            @Override
            public void run() {
                Job modpack = new Job(downloadModel.getDownloadListModPack(),
                        view);
                modpack.startDownload();
            }
        };
        t.start();
        t.join();
        System.out.println("FINISH MODPACK UPDATE");

        final Version v = Version.getVersion(new File(downloadModel
                .getModPackDir(), "bin/minecraft.json"));
        launchModel.setVersion(v);

        if (v != null) {
            view.setStatut("Download Requiere Lib ...");
            t = new Thread("Lib") {
                @Override
                public void run() {
                    Job modpack;
                    try {
                        modpack = new Job(v.getRequiredDownloadables(
                                OperatingSystem.getCurrentPlatform(),
                                Proxy.NO_PROXY, downloadModel.getModPackDir()
                                        .getParentFile(), false), view);
                        modpack.startDownload();
                    } catch (MalformedURLException e) {

                        e.printStackTrace();
                    }

                }
            };
            t.start();
            t.join();
            System.out.println("FINISH LIB UPDATE");

            view.setStatut("Download Requiere Assets ...");
            t = new Thread("Assets") {

                @Override
                public void run() {
                    Job modpack = new Job(AssetIndex.getResourceFiles(v
                            .getAssets(), downloadModel.getModPackDir()
                            .getParentFile()), view);
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
            try {
                this.propertyChange(this, "MASK", true);
                view.setVisible(false);
                launch.startGame();
            } catch (IOException e) {

                e.printStackTrace();
            }

        }

    }

    public void selectModPack(RemotePack remotePack) {
        this.setModelProperty(PACK, remotePack);
        this.propertyChange(this, PACK, null);
    }

    public LaunchModel getLaunchModel() {
        return launchModel;
    }

    public void savePreference() {
        try {
            File pref = new File(Util.getWorkingDirectory(), "preference.json");
            pref.createNewFile();
            FileUtils.write(pref,
                    new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                            .create().toJson(launchModel));
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static MCLauncherProfiles getAuthData() {
        return authData;
    }

}
