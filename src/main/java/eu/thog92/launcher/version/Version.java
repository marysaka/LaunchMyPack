package eu.thog92.launcher.version;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import eu.thog92.launcher.download.ChecksummedDownloadable;
import eu.thog92.launcher.download.Downloadable;
import eu.thog92.launcher.util.OperatingSystem;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.*;

public class Version
{

    private String inheritsFrom;
    private String id;
    private Date time;
    private Date releaseTime;
    private ReleaseType type;
    private String minecraftArguments;
    private List<Library> libraries;
    private String mainClass;
    private int minimumLauncherVersion;
    private String incompatibilityReason;
    private String assets;
    private List<CompatibilityRule> compatibilityRules;
    private String jar;
    private Version savableVersion;
    private transient boolean synced = false;

    public Version()
    {
    }

    public Version(Version version)
    {
        this.inheritsFrom = version.inheritsFrom;
        this.id = version.id;
        this.time = version.time;
        this.releaseTime = version.releaseTime;
        this.type = version.type;
        this.minecraftArguments = version.minecraftArguments;
        this.mainClass = version.mainClass;
        this.minimumLauncherVersion = version.minimumLauncherVersion;
        this.incompatibilityReason = version.incompatibilityReason;
        this.assets = version.assets;
        this.jar = version.jar;
        if (version.libraries != null)
        {
            this.libraries = new ArrayList<Library>();
            for (Library library : version.getLibraries())
            {
                this.libraries.add(new Library(library));
            }
        }
        if (version.compatibilityRules != null)
        {
            this.compatibilityRules = new ArrayList<CompatibilityRule>();
            for (CompatibilityRule compatibilityRule : version.compatibilityRules)
            {
                this.compatibilityRules.add(new CompatibilityRule(compatibilityRule));
            }
        }
    }

    public static Version getVersion(File json)
    {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(new LowerCaseEnumTypeAdapterFactory());
        builder.registerTypeAdapter(Date.class, new DateTypeAdapter());
        builder.registerTypeAdapter(ReleaseType.class, new ReleaseTypeAdapterFactory());
        builder.enableComplexMapKeySerialization();
        builder.setPrettyPrinting();
        try
        {
            return builder.create().fromJson(new FileReader(json), Version.class);
        } catch (JsonSyntaxException e)
        {

            e.printStackTrace();
        } catch (IOException e)
        {

            e.printStackTrace();
        }
        return null;
    }

    public String getId()
    {
        return this.id;
    }

    public ReleaseType getType()
    {
        return this.type;
    }

    public void setType(ReleaseType type)
    {
        if (type == null)
        {
            throw new IllegalArgumentException("Release type cannot be null");
        }
        this.type = type;
    }

    public Date getUpdatedTime()
    {
        return this.time;
    }

    public Date getReleaseTime()
    {
        return this.releaseTime;
    }

    public List<Library> getLibraries()
    {
        return this.libraries;
    }

    public String getMainClass()
    {
        return this.mainClass;
    }

    public String getJar()
    {
        return this.jar == null ? this.id : this.jar;
    }

    public Collection<Library> getRelevantLibraries()
    {
        List<Library> result = new ArrayList<Library>();
        for (Library library : this.libraries)
        {
            if (library.appliesToCurrentEnvironment())
            {
                result.add(library);
            }
        }
        return result;
    }

    public Collection<File> getClassPath(OperatingSystem os, File base)
    {
        Collection<Library> libraries = getRelevantLibraries();
        Collection<File> result = new ArrayList<File>();
        for (Library library : libraries)
        {
            if (library.getNatives() == null)
            {
                result.add(new File(base, "libraries/" + library.getArtifactPath()));
            }
        }
        //result.add(new File(base, "versions/" + getJar() + "/" + getJar() + ".jar"));

        return result;
    }

    public Set<String> getRequiredFiles(OperatingSystem os)
    {
        Set<String> neededFiles = new HashSet<String>();
        for (Library library : getRelevantLibraries())
        {
            if (library.getNatives() != null)
            {
                String natives = library.getNatives().get(os);
                if (natives != null)
                {
                    neededFiles.add("libraries/" + library.getArtifactPath(natives));
                }
            } else
            {
                neededFiles.add("libraries/" + library.getArtifactPath());
            }
        }
        return neededFiles;
    }

    public Set<Downloadable> getRequiredDownloadables(OperatingSystem os, Proxy proxy, File targetDirectory, boolean ignoreLocalFiles)
            throws MalformedURLException
    {
        Set<Downloadable> neededFiles = new HashSet<Downloadable>();
        for (Library library : getRelevantLibraries())
        {
            String file = null;
            if (library.getNatives() != null)
            {
                String natives = library.getNatives().get(os);
                if (natives != null)
                {
                    file = library.getArtifactPath(natives);
                }
            } else
            {
                file = library.getArtifactPath();
            }
            if (file != null)
            {

                URL url = new URL(library.getDownloadUrl() + file);
                File local = new File(targetDirectory, "libraries/" + file);
                if ((!local.isFile()) || (!library.hasCustomUrl()))
                {
                    neededFiles.add(new ChecksummedDownloadable(proxy, url, local, ignoreLocalFiles));
                }
            }

        }
        return neededFiles;
    }

    @Override
    public String toString()
    {
        return "Version{id='" + this.id + '\'' + ", updatedTime=" + this.time + ", releasedTime=" + this.time + ", type=" + this.type + ", libraries=" + this.libraries + ", mainClass='" + this.mainClass + '\'' + ", jar='" + this.jar + '\'' + ", minimumLauncherVersion=" + this.minimumLauncherVersion + '}';
    }

    public String getMinecraftArguments()
    {
        return this.minecraftArguments;
    }

    public int getMinimumLauncherVersion()
    {
        return this.minimumLauncherVersion;
    }

    public boolean appliesToCurrentEnvironment()
    {
        if (this.compatibilityRules == null)
        {
            return true;
        }
        CompatibilityRule.Action lastAction = CompatibilityRule.Action.DISALLOW;
        for (CompatibilityRule compatibilityRule : this.compatibilityRules)
        {
            CompatibilityRule.Action action = compatibilityRule.getAppliedAction();
            if (action != null)
            {
                lastAction = action;
            }
        }
        return lastAction == CompatibilityRule.Action.ALLOW;
    }

    public String getIncompatibilityReason()
    {
        return this.incompatibilityReason;
    }

    public boolean isSynced()
    {
        return this.synced;
    }

    public void setSynced(boolean synced)
    {
        this.synced = synced;
    }

    public String getAssets()
    {
        return this.assets;
    }

    public String getInheritsFrom()
    {
        return this.inheritsFrom;
    }

    public Version getSavableVersion()
    {
        return this.savableVersion;
    }
}
