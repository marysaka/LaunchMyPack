package eu.thog92.launcher.launch;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.thog92.launcher.download.Downloadable;
import eu.thog92.launcher.util.LogAgent;
import eu.thog92.launcher.util.OperatingSystem;
import eu.thog92.launcher.version.AssetIndex;
import eu.thog92.launcher.version.AssetObject;
import eu.thog92.launcher.version.ExtractRules;
import eu.thog92.launcher.version.Library;
import eu.thog92.launcher.version.Version;
import fr.wherecraft.launcher.model.DownloadModel;
import fr.wherecraft.launcher.model.LaunchModel;

public class GameLaunch implements JavaProcessRunnable
{
    private Gson gson;
    private LaunchModel launchModel;
    private DownloadModel downloadModel;
    private File nativeDir;
    private LogAgent log;
    private File gameDir;
    private File commonDir;
    
    public GameLaunch(LaunchModel m, DownloadModel m2, LogAgent log)
    {
        this.log = log;
        this.launchModel = m;
        this.downloadModel = m2;
        this.gameDir = downloadModel.getModPackDir();
        this.commonDir = gameDir.getParentFile();
    }
    
    public void startGame() throws IOException
    {
        gson = new GsonBuilder().create();
        OperatingSystem os = OperatingSystem.getCurrentPlatform();
        
        nativeDir = new File(gameDir, "bin/natives");
        
        if (nativeDir.exists())
        {
            if (!nativeDir.delete())
            {
                new Exception("Native cannot be remove !");
            }
        }
        if (!nativeDir.isDirectory())
            nativeDir.mkdirs();
        
        try
        {
            unpackNatives(nativeDir);
        } catch (IOException e)
        {
            // launcher.getLogAgent().logSevereException(
            // "Couldn't unpack natives!", e);
            e.printStackTrace();
            return;
        }
        
        JavaProcessLauncher processLauncher = new JavaProcessLauncher(
                os.getJavaDir(), new String[0]);
        processLauncher.directory(gameDir);
        
        if (os.equals(OperatingSystem.OSX))
            processLauncher.addCommands(new String[]
            {
                    "-Xdock:icon="
                            + getAssetObject("icons/minecraft.icns")
                                    .getAbsolutePath(), "-Xdock:name=FTF" });
        else if (os.equals(OperatingSystem.WINDOWS))
        {
            processLauncher
                    .addCommands(new String[]
                    { "-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump" });
        }
        
        boolean is32Bit = "32"
                .equals(System.getProperty("sun.arch.data.model"));
        String defaultArgument = is32Bit ? "-Xmx1G" : "-Xmx1G";
        processLauncher.addSplitCommands(defaultArgument);
        processLauncher.addSplitCommands("-Dfml.ignoreInvalidMinecraftCertificates=true -Dfml.ignorePatchDiscrepancies=true");
        processLauncher.addCommands(new String[]
        { "-Djava.library.path=" + nativeDir.getAbsolutePath() });
        processLauncher.addCommands(new String[]
        { "-cp", constructClassPath(launchModel.getVersion()) });
        processLauncher.addCommands(new String[]
        { launchModel.getVersion().getMainClass() });
        File assetsDir = new File(commonDir, "assets");
        File virtualRoot = new File(new File(assetsDir, "virtual"), "legacy");
        String[] args = getMinecraftArguments(launchModel.getVersion(),
                gameDir, virtualRoot);
        if (args == null)
            return;
        processLauncher.addCommands(args);
        
        try
        {
            JavaProcess process = processLauncher.start();
            process.safeSetExitRunnable(this);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        
        log.logInfo("Running "
                + StringUtils.join(processLauncher.getFullCommands(), " "));
    }
    
    private File reconstructAssets()
            throws IOException
          {
            File assetsDir = new File(gameDir, "assets");
            File indexDir = new File(assetsDir, "indexes");
            File objectDir = new File(assetsDir, "objects");
            String assetVersion = launchModel.getVersion().getAssets() == null ? "legacy" : launchModel.getVersion().getAssets();
            File indexFile = new File(indexDir, assetVersion + ".json");
            File virtualRoot = new File(new File(assetsDir, "virtual"), assetVersion);
            if (!indexFile.isFile())
            {
              log.logWarning("No assets index file " + virtualRoot + "; can't reconstruct assets");
              return virtualRoot;
            }
            AssetIndex index = (AssetIndex)this.gson.fromJson(FileUtils.readFileToString(indexFile, Charsets.UTF_8), AssetIndex.class);
            if (index.isVirtual())
            {
              log.logInfo("Reconstructing virtual assets folder at " + virtualRoot);
              for (Map.Entry<String, AssetObject> entry : index.getObjectMap().entrySet())
              {
                File target = new File(virtualRoot, (String)entry.getKey());
                File original = new File(new File(objectDir, ((AssetObject)entry.getValue()).getHash().substring(0, 2)), ((AssetObject)entry.getValue()).getHash());
                if (!target.isFile()) {
                  FileUtils.copyFile(original, target, false);
                }
              }
            }
            return virtualRoot;
          }
    
    private String constructClassPath(final Version version)
    {
        StringBuilder result = new StringBuilder();
        Collection<File> classPath = version.getClassPath(
                OperatingSystem.getCurrentPlatform(), commonDir);
        String separator = System.getProperty("path.separator");
        
        result.append(downloadModel.getMinecraftJarPath().getAbsolutePath());
        for (File file : classPath)
        {
            if (result.length() > 0)
                result.append(separator);
            result.append(file.getAbsolutePath());
        }
        
        return result.toString();
    }
    
    private String[] getMinecraftArguments(Version version, File gameDirectory,
            File assetsDirectory)
    {
        if (version.getMinecraftArguments() == null)
        {
            return null;
        }
        
        Map<String, String> map = new HashMap<String, String>();
        StrSubstitutor substitutor = new StrSubstitutor(map);
        String[] split = version.getMinecraftArguments().split(" ");
        
        map.put("auth_access_token", "0");
        map.put("auth_session", String.format("token:%s:%s", new Object[]
        { "0", "1" }));
        
        map.put("auth_player_name", launchModel.getUsername());
        map.put("auth_uuid", "0");
        map.put("user_type", launchModel.getUsername());
        
        map.put("profile_name", "Wherecraft");
        map.put("version_name", version.getId());
        
        map.put("game_directory", gameDirectory.getAbsolutePath());
        map.put("game_assets", assetsDirectory.getAbsolutePath());
        
        map.put("assets_root", new File(commonDir, "assets").getAbsolutePath());
        map.put("assets_index_name", launchModel.getVersion().getAssets());
        map.put("user_properties", "{}");
        
        for (int i = 0; i < split.length; i++)
        {
            split[i] = substitutor.replace(split[i]);
        }
        
        return split;
    }
    
    private File getAssetObject(String name) throws IOException
    {
        File assetsDir = new File(commonDir, "assets");
        File indexDir = new File(assetsDir, "indexes");
        File objectsDir = new File(assetsDir, "objects");
        String assetVersion = launchModel.getVersion().getAssets();
        File indexFile = new File(indexDir, assetVersion + ".json");
        AssetIndex index = (AssetIndex) gson.fromJson(
                FileUtils.readFileToString(indexFile, Charsets.UTF_8),
                AssetIndex.class);
        
        String hash = ((AssetObject) index.getObjectMap().get(name)).getHash();
        return new File(objectsDir, hash.substring(0, 2) + "/" + hash);
    }
    
    private void unpackNatives(File targetDir) throws IOException
    {
        OperatingSystem os = OperatingSystem.getCurrentPlatform();
        Collection<Library> libraries = launchModel.getVersion()
                .getRelevantLibraries();
        
        for (Library library : libraries)
        {
            Map<OperatingSystem, String> nativesPerOs = library.getNatives();
            
            if ((nativesPerOs != null) && (nativesPerOs.get(os) != null))
            {
                File file = new File(commonDir,
                        "libraries/"
                                + library.getArtifactPath((String) nativesPerOs
                                        .get(os)));
                ZipFile zip = new ZipFile(file);
                ExtractRules extractRules = library.getExtractRules();
                try
                {
                    Enumeration<? extends ZipEntry> entries = zip.entries();
                    
                    while (entries.hasMoreElements())
                    {
                        ZipEntry entry = (ZipEntry) entries.nextElement();
                        
                        if ((extractRules != null)
                                && (!extractRules
                                        .shouldExtract(entry.getName())))
                        {
                            continue;
                        }
                        File targetFile = new File(targetDir, entry.getName());
                        if (targetFile.getParentFile() != null)
                            targetFile.getParentFile().mkdirs();
                        
                        if (!entry.isDirectory())
                        {
                            BufferedInputStream inputStream = new BufferedInputStream(
                                    zip.getInputStream(entry));
                            
                            byte[] buffer = new byte[2048];
                            FileOutputStream outputStream = new FileOutputStream(
                                    targetFile);
                            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                                    outputStream);
                            try
                            {
                                int length;
                                while ((length = inputStream.read(buffer, 0,
                                        buffer.length)) != -1)
                                    bufferedOutputStream.write(buffer, 0,
                                            length);
                            } finally
                            {
                                Downloadable
                                        .closeSilently(bufferedOutputStream);
                                Downloadable.closeSilently(outputStream);
                                Downloadable.closeSilently(inputStream);
                            }
                        }
                    }
                } finally
                {
                    zip.close();
                }
            }
        }
    }
    
    @Override
    public void onJavaProcessEnded(final JavaProcess paramJavaProcess)
    {
        // TODO Auto-generated method stub
        final int exitCode = paramJavaProcess.getExitCode();
        log.logInfo("Game exit with code " + exitCode);
        
        System.exit(0);
    }
}
