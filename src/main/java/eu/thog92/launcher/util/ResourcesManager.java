package eu.thog92.launcher.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ResourcesManager
{
    public static BufferedImage getImage(String name) throws IOException
    {
        return ImageIO.read(ResourcesManager.class.getResource("/assets/" + Constants.getLauncherName().toLowerCase() + "/img/" + name));
    }

    public static Icon getIcon(String name) throws IOException
    {
        return new ImageIcon(getImage(name));
    }
}
