package fr.wherecraft.graphics;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JButton;

import eu.thog92.launcher.util.ResourcesManager;

public class BasicButton extends JButton implements MouseListener
{

    private static final long serialVersionUID = -7072683448027860055L;
    protected Image img;
    protected String texture;
    
    protected BasicButton()
    {
        
    }
    public BasicButton(String t)
    {
        super();
        this.setBorderPainted(false);
        this.setContentAreaFilled(true);
        this.setOpaque(false);
        this.texture = t;
        try {
            this.img = ResourcesManager.getImage(texture + ".png");
        } catch (IOException e) {
                e.printStackTrace();
        }
        this.addMouseListener(this);
    }
    
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D)g;
        GradientPaint gp = new GradientPaint(0, 0, Color.blue, 0, 20, Color.cyan, true);
        g2d.setPaint(gp);
        g2d.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
        g2d.setColor(Color.black);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {}
    
    @Override
    public void mousePressed(MouseEvent e) {}
    
    @Override
    public void mouseReleased(MouseEvent e) {}
    
    @Override
    public void mouseEntered(MouseEvent e) {}
    
    @Override
    public void mouseExited(MouseEvent e) {}
    
}
