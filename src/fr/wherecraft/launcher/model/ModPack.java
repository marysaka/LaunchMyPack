package fr.wherecraft.launcher.model;

import java.io.File;

public class ModPack
{
    private String name;
    private File workdir;
    private RemotePack pack;
    public ModPack(RemotePack pack, File baseDir)
    {
        this.pack = pack;
        this.workdir = new File(baseDir, pack.getName().replace(" ", "-"));
        this.name = pack.getName();
    }

    public String getName()
    {
        return name;
    }

    public File getWorkdir()
    {
        return workdir;
    }

    public String getDownloadURL()
    {
        return pack.getURL();
    }
}
