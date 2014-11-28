package eu.thog92.launcher.demoimpl.model;

import com.google.gson.annotations.Expose;

import eu.thog92.launcher.model.AbstractModel;
import eu.thog92.launcher.version.Version;

public class LaunchModel extends AbstractModel
{
    @Expose private String username;
    private Version version;
    @Expose private int ram;
    
    private String token;
    
    
    public LaunchModel() { this.ram = 1024;}
    public void setUsername(String u)
    {
        this.username = u;
    }
    
    public void setVersion(Version v)
    {
        this.version = v;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public synchronized Version getVersion()
    {
        return version;
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
    public void setToken(String token)
    {
        this.token = token;
    }
    
    
}
