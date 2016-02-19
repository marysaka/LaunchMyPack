package eu.thog92.launcher.model;

import eu.thog92.launcher.download.Downloadable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadModel extends AbstractModel
{
    private static ModPack modPack;
    private List<Downloadable> downloadmodpack;

    public DownloadModel()
    {
        this.downloadmodpack = new ArrayList<Downloadable>();
    }

    public synchronized List<Downloadable> getDownloadListModPack()
    {
        return downloadmodpack;

    }

    public synchronized void addDownloadListModPack(Downloadable d)
    {
        downloadmodpack.add(d);
    }

    public synchronized void setModPack(RemotePack remote)
    {
        modPack = new ModPack(remote);
    }

    public void setUsername(String ignore)
    {
    }

    public synchronized File getModPackDir()
    {
        return modPack.getWorkdir().getAbsoluteFile();
    }

    public synchronized String getDownloadURL()
    {
        return modPack.getDownloadURL();
    }

    public synchronized File getMinecraftJarPath()
    {
        return new File(modPack.getWorkdir(), "bin/minecraft.jar");
    }
}
