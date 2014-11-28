package eu.thog92.launcher.version;

import java.util.HashMap;

public class MCLauncherProfiles
{
    private String selectedProfile;
    private String clientToken;
    private HashMap<String, Authentication> authenticationDatabase;
    private String selectedUser;
    
    public String getSelectedProfile()
    {
        return selectedProfile;
    }
    
    public String getClientToken()
    {
        return clientToken;
    }
    
    public HashMap<String, Authentication> getAuthenticationDatabase()
    {
        return authenticationDatabase;
    }
    
    public String getSelectedUser()
    {
        return selectedUser;
    }
}
