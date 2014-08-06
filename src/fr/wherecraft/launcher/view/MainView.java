package fr.wherecraft.launcher.view;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import eu.thog92.launcher.util.Constants;
import eu.thog92.launcher.util.ResourcesManager;
import fr.wherecraft.graphics.BasicButton;
import fr.wherecraft.graphics.DrawedButton;
import fr.wherecraft.launcher.controller.MainController;
import fr.wherecraft.launcher.model.RemotePack;

public class MainView extends JFrame implements IView
{
    private static final long serialVersionUID = 4433311775544466699L;
    private JTextField userName = new JTextField();
    private DrawedButton logButt = new DrawedButton("logbutton", true);
    private DrawedButton optButt = new DrawedButton("optbutton", false);
    private BasicButton exitButt = new BasicButton("cross");
    private BasicButton minButt = new BasicButton("min");
    private MainController controller;
    protected int posX, posY;
    private static MainView instance;
    
    public MainView(MainController c)
    {
        instance = this;
        this.controller = c;
        setLayout(null);
        
        JLabel background = new JLabel("Chargement du Background ...");
        background.setVerticalAlignment(0);
        background.setHorizontalAlignment(0);
        background.setFont(new Font("Arial", 3, 11));
        background.setBounds(0, 0, 1280, 720);
        try
        {
            background.setIcon(ResourcesManager.getIcon("background.png"));
            this.setIconImage(ResourcesManager.getImage("favicon.png"));
        } catch (IOException e1)
        {
            e1.printStackTrace();
        }
        this.setUndecorated(true);
        this.setSize(1280, 720);
        // this.setPreferredSize(new Dimension(1280, 720));
        
        this.exitButt.setBounds(1244, 10, 19, 19);
        this.minButt.setBounds(1218, 10, 19, 19);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        this.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(final MouseEvent e)
            {
                if (e.getY() < 40)
                {
                    posX = e.getX();
                    posY = e.getY();
                }
            }
        });
        this.addMouseMotionListener(new MouseAdapter()
        {
            public void mouseDragged(final MouseEvent evt)
            {
                if (evt.getY() < 40)
                {
                    setLocation(evt.getXOnScreen() - posX, evt.getYOnScreen()
                            - posY);
                }
            }
        });
        
        logButt.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                new Thread("Login")
                {
                    @Override
                    public void run()
                    {
                        controller.pressPlayButton(userName.getText());
                    }
                }.start();
                
            }
            
        });
        
        exitButt.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
            
        });
        
        minButt.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                instance.setState(JFrame.ICONIFIED);
            }
            
        });
        add(this.createLoginPanel());
        add(this.exitButt);
        add(this.minButt);
        add(background);
    }
    
    private final JPanel createLoginPanel()
    {
        JPanel panel = new JPanel();
        panel.setBounds(522, 240, 666, 366);
        panel.setOpaque(false);
        panel.setLayout(null);
        this.userName.setHorizontalAlignment(JLabel.CENTER);
        this.userName.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        this.userName.setFont(new Font("Arial", 3, 33));
        this.userName.setBounds(16, 15, 466, 76);
        this.userName.setOpaque(false);
        this.logButt.setBounds(20, 100, 200, 70);
        this.logButt.setOpaque(true);
        this.optButt.setBounds(279, 100, 200, 70);
        panel.add(this.userName);
        panel.add(this.logButt);
        panel.add(this.optButt);
        return panel;
    }
    
    @Override
    public void modelPropertyChange(final PropertyChangeEvent evt)
    {
        // System.out.println(evt.getPropertyName() + ": " + evt.getNewValue());
        if (evt.getPropertyName().equals(MainController.ELEMENT_BUTTON_PLAY))
        {
            new Thread("View")
            {
                @Override
                public void run()
                {
                    ModPackSelectView pack = new ModPackSelectView(instance, (RemotePack[]) evt.getNewValue(), controller);
                    pack.setVisible(true);
                }
            }.start();

        } 
        else if (evt.getPropertyName().equals(
                MainController.PACK))
        {
            logButt.setEnabled(false);
            new Thread("Login")
            {
                @Override
                public void run()
                {
                    try
                    {
                        controller.manageStart();
                    } catch (InterruptedException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        
        else if (evt.getPropertyName().equals("DISPLAY_ERROR"))
            JOptionPane.showMessageDialog(this, evt.getNewValue(),
                    Constants.LAUNCHER_NAME + " -  Error",
                    JOptionPane.ERROR_MESSAGE);
        else if (evt.getPropertyName().equals("MASK"))
            this.setVisible(false);
    }
}
