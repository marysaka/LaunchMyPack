package eu.thog92.launcher.version;


import java.io.File;
import java.util.List;


public class Profile
  implements Comparable<Profile>
{
  public static final String DEFAULT_JRE_ARGUMENTS_64BIT = "-Xmx1G -XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode -XX:-UseAdaptiveSizePolicy -Xmn128M";
  public static final String DEFAULT_JRE_ARGUMENTS_32BIT = "-Xmx512M -XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode -XX:-UseAdaptiveSizePolicy -Xmn128M";
  public static final Resolution DEFAULT_RESOLUTION = new Resolution(854, 480);
  private String name;
  private File gameDir;
  private String lastVersionId;
  private String javaDir;
  private String javaArgs;
  private Resolution resolution;
  private String playerUUID;
  private Boolean useHopperCrashService;
  private String launcherVisibilityOnGameClose;
  private List<String> allowedReleaseTypes;
  
  public Profile() {}
  
  public Profile(Profile copy)
  {
    this.name = copy.name;
    this.gameDir = copy.gameDir;
    this.playerUUID = copy.playerUUID;
    this.lastVersionId = copy.lastVersionId;
    this.javaDir = copy.javaDir;
    this.javaArgs = copy.javaArgs;
    this.resolution = (copy.resolution == null ? null : new Resolution(copy.resolution));
    this.useHopperCrashService = copy.useHopperCrashService;
  }
  
  public Profile(String name)
  {
    this.name = name;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public File getGameDir()
  {
    return this.gameDir;
  }
  
  public void setGameDir(File gameDir)
  {
    this.gameDir = gameDir;
  }
  
  public void setLastVersionId(String lastVersionId)
  {
    this.lastVersionId = lastVersionId;
  }
  
  public void setJavaDir(String javaDir)
  {
    this.javaDir = javaDir;
  }
  
  public void setJavaArgs(String javaArgs)
  {
    this.javaArgs = javaArgs;
  }
  
  public String getLastVersionId()
  {
    return this.lastVersionId;
  }
  
  public String getJavaArgs()
  {
    return this.javaArgs;
  }
  
  public String getJavaPath()
  {
    return this.javaDir;
  }
  
  public Resolution getResolution()
  {
    return this.resolution;
  }
  
  public void setResolution(Resolution resolution)
  {
    this.resolution = resolution;
  }
  
  @Deprecated
  public String getPlayerUUID()
  {
    return this.playerUUID;
  }
  
  @Deprecated
  public void setPlayerUUID(String playerUUID)
  {
    this.playerUUID = playerUUID;
  }
  
  public boolean getUseHopperCrashService()
  {
    return this.useHopperCrashService == null;
  }
  
  public void setUseHopperCrashService(boolean useHopperCrashService)
  {
    this.useHopperCrashService = (useHopperCrashService ? null : Boolean.valueOf(false));
  }
  


  public int compareTo(Profile o)
  {
    if (o == null) {
      return -1;
    }
    return getName().compareTo(o.getName());
  }
  
  public static class Resolution
  {
    private int width;
    private int height;
    
    public Resolution() {}
    
    public Resolution(Resolution resolution)
    {
      this(resolution.getWidth(), resolution.getHeight());
    }
    
    public Resolution(int width, int height)
    {
      this.width = width;
      this.height = height;
    }
    
    public int getWidth()
    {
      return this.width;
    }
    
    public int getHeight()
    {
      return this.height;
    }
  }
}
