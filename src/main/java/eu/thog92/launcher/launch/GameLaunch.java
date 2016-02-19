package eu.thog92.launcher.launch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.thog92.launcher.download.Downloadable;
import eu.thog92.launcher.model.DownloadModel;
import eu.thog92.launcher.model.LaunchModel;
import eu.thog92.launcher.util.*;
import eu.thog92.launcher.version.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
        this.commonDir = Util.getMinecraftDir();
    }

    public void startGame() throws IOException
    {
        gson = new GsonBuilder().create();
        OperatingSystem os = OperatingSystem.getCurrentPlatform();

        nativeDir = new File(gameDir, "bin/natives");

        if (nativeDir.exists())
        {
            if (!IOUtils.delete(nativeDir))
            {
                throw new IOException("Native cannot be remove!");
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
                                    .getAbsolutePath(), "-Xdock:name=FTF"});
        else if (os.equals(OperatingSystem.WINDOWS))
        {
            processLauncher
                    .addCommands(new String[]
                            {"-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump"});
        }


        String defaultArgument = "-Xmx" + launchModel.getRAM() + "m";
        processLauncher.addSplitCommands(defaultArgument);
        processLauncher.addSplitCommands("-XX:MaxPermSize=128m");
        processLauncher.addSplitCommands("-Dfml.ignoreInvalidMinecraftCertificates=true -Dfml.ignorePatchDiscrepancies=true");
        processLauncher.addCommands(new String[]
                {"-Djava.library.path=" + nativeDir.getAbsolutePath()});
        processLauncher.addCommands(new String[]
                {"-cp", constructClassPath(launchModel.getVersion())});
        processLauncher.addCommands(new String[]
                {launchModel.getVersion().getMainClass()});
        File virtualRoot = reconstructAssets();
        String[] args = getMinecraftArguments(launchModel.getVersion(),
                gameDir, virtualRoot);
        if (args == null)
            return;
        processLauncher.addCommands(args);

        try
        {
            JavaProcess process = processLauncher.start();
            log.logInfo("Running "
                    + StringUtils.join(processLauncher.getFullCommands(), " "));
            System.gc(); //FIXME: WTF
            process.safeSetExitRunnable(this);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private File reconstructAssets()
            throws IOException
    {
        File assetsDir = new File(commonDir, "assets");
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
        AssetIndex index = this.gson.fromJson(StringUtils.readFileToString(indexFile, Charset.defaultCharset()), AssetIndex.class);
        if (index.isVirtual())
        {
            log.logInfo("Reconstructing virtual assets folder at " + virtualRoot);
            for (Map.Entry<String, AssetObject> entry : index.getObjectMap().entrySet())
            {
                File target = new File(virtualRoot, entry.getKey());
                File original = new File(new File(objectDir, entry.getValue().getHash().substring(0, 2)), entry.getValue().getHash());
                if (!target.isFile())
                    Files.copy(original.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
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

        System.out.println(classPath);

        for (File file : classPath)
        {
            if (result.length() > 0)
                result.append(separator);
            result.append(file.getAbsolutePath());
        }
        result.append(separator);
        result.append(downloadModel.getMinecraftJarPath().getAbsolutePath());

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
        String[] split = version.getMinecraftArguments().split(" ");

        map.put("auth_access_token", launchModel.getToken());
        map.put("auth_session", String.format("token:%s:%s", launchModel.getToken(), launchModel.getProfileID()));

        map.put("auth_player_name", launchModel.getUsername());
        map.put("auth_uuid", launchModel.getProfileID());
        map.put("user_type", launchModel.getUsername());

        map.put("profile_name", "FTF");
        map.put("version_name", version.getId());

        map.put("game_directory", gameDirectory.getAbsolutePath());
        map.put("game_assets", assetsDirectory.getAbsolutePath());

        map.put("assets_root", new File(commonDir, "assets").getAbsolutePath());
        map.put("assets_index_name", launchModel.getVersion().getAssets());
        map.put("user_properties", "{}");

        for (int i = 0; i < split.length; i++)
        {
            split[i] = replaceData(map, split[i]);
        }

        return split;
    }

    private String replaceData(Map<String, String> map, String arg)
    {
        for (Map.Entry<String, String> entry : map.entrySet())
        {
            arg = arg.replace("${" + entry.getKey() + "}", entry.getValue());
        }
        return arg;
    }

    private File getAssetObject(String name) throws IOException
    {
        File assetsDir = new File(commonDir, "assets");
        File indexDir = new File(assetsDir, "indexes");
        File objectsDir = new File(assetsDir, "objects");
        String assetVersion = launchModel.getVersion().getAssets();
        File indexFile = new File(indexDir, assetVersion + ".json");
        AssetIndex index = gson.fromJson(
                StringUtils.readFileToString(indexFile, "UTF-8"),
                AssetIndex.class);

        String hash = index.getObjectMap().get(name).getHash();
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
                                + library.getArtifactPath(nativesPerOs
                                .get(os)));
                ZipFile zip = new ZipFile(file);
                ExtractRules extractRules = library.getExtractRules();
                try
                {
                    Enumeration<? extends ZipEntry> entries = zip.entries();

                    while (entries.hasMoreElements())
                    {
                        ZipEntry entry = entries.nextElement();

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
