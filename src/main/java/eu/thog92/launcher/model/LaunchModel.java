package eu.thog92.launcher.model;

import com.google.gson.annotations.Expose;
import eu.thog92.launcher.version.Version;

public class LaunchModel extends AbstractModel
{
    @Expose
    private String username;
    private Version version;
    @Expose
    private int ram;

    private String token;
    private String profileID;


    public LaunchModel()
    {
        this.ram = 1024;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String u)
    {
        this.username = u;
    }

    public synchronized Version getVersion()
    {
        return version;
    }

    public void setVersion(Version v)
    {
        this.version = v;
    }

    public void setModPack(Object[] ignore)
    {

    }

    public int getRAM()
    {
        return ram;
    }

    public void setRAM(int value)
    {
        this.ram = value;
    }

    public String getToken()
    {
        return token;
    }

    public String getProfileID()
    {
        return profileID;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public void setProfileID(String profileID)
    {
        this.profileID = profileID;
    }
}
