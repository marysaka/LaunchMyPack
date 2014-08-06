package fr.wherecraft.graphics;

import java.awt.event.MouseEvent;
import java.io.IOException;

import eu.thog92.launcher.util.ResourcesManager;

public class DrawedButton extends BasicButton
{
    
    private static final long serialVersionUID = 5201831784690615553L;
    private boolean hasDisabledButtonTexture;
    public DrawedButton(String t, boolean dis)
    {
        this.setBorderPainted(false);
        this.setContentAreaFilled(true);
        this.setOpaque(false);
        this.texture = t;
        this.hasDisabledButtonTexture = dis;
        try {
            this.img = ResourcesManager.getImage(texture + "_released.png");
        } catch (IOException e) {
                e.printStackTrace();
        }
        this.addMouseListener(this);
    }

    public void mouseClicked(MouseEvent event) {}
    
    public void mouseEntered(MouseEvent event) {removeAll();try{img = ResourcesManager.getImage(texture + "_entered.png");} catch(IOException e){e.printStackTrace();}}
    
    public void mouseExited(MouseEvent event) {removeAll();try{img = ResourcesManager.getImage(texture + "_released.png");} catch(IOException e){e.printStackTrace();}}
    
    public void mousePressed(MouseEvent event) {removeAll();try{img = ResourcesManager.getImage(texture + "_pressed.png");} catch(IOException e){e.printStackTrace();}}
         
    public void mouseReleased(MouseEvent event) {removeAll();try{img = ResourcesManager.getImage(texture + "_released.png");} catch(IOException e){e.printStackTrace();}}
    
    public void disableButton()
    {
        this.setEnabled(false);
        removeAll();
        
        if(hasDisabledButtonTexture)
        {
            try
            {
                img = ResourcesManager.getImage(texture + "_disabled.png");
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }
}
