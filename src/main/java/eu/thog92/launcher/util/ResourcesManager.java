package eu.thog92.launcher.util;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class ResourcesManager
{
    public static BufferedImage getImage(String name) throws IOException
    {
        return ImageIO.read(ResourcesManager.class.getResource("/assets/" + Constants.LAUNCHER_NAME.toLowerCase() + "/img/" + name));
    }

    public static Icon getIcon(String name) throws IOException
    {
        return new ImageIcon(getImage(name));
    }
    
}
