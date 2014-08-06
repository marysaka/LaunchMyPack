package fr.wherecraft.launcher.model;

import eu.thog92.launcher.model.AbstractModel;
import eu.thog92.launcher.version.Version;

public class LaunchModel extends AbstractModel
{
    private String username;
    private Version version;
    
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
        System.out.println("LEHOIHF ");
    }
    
    
}
