package eu.thog92.launcher.version;

import java.util.HashMap;

@SuppressWarnings("unused")
public class Authentication
{
    private String displayName;
    private String accessToken;
    private String userid;
    private String uuid;
    private String username;
    public String getDisplayName()
    {
        return displayName;
    }
    
    public String getAccessToken()
    {
        return accessToken;
    }
    
    public String getUserid()
    {
        return userid;
    }
    public void setUserid(String userid)
    {
        this.userid = userid;
    }
    public String getUsername()
    {
        return username;
    }
    
    public String getUuid()
    {
        return uuid;
    }
}
