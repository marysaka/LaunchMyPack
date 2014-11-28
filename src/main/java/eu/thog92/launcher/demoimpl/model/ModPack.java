package eu.thog92.launcher.demoimpl.model;

import java.io.File;

import eu.thog92.launcher.util.Util;

public class ModPack
{
    private String name;
    private File workdir;
    private RemotePack pack;
    public ModPack(RemotePack pack)
    {
        this.pack = pack;
        this.workdir = new File(Util.getWorkingDirectory(), pack.getName().replace(" ", "-"));
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
