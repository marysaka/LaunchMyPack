package eu.thog92.launcher.demoimpl.view;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import eu.thog92.launcher.demoimpl.controller.MainController;
import eu.thog92.launcher.demoimpl.model.RemotePack;
import eu.thog92.launcher.util.Constants;

public class MainView extends JFrame implements IView {

    /**
     *
     */
    private static final long serialVersionUID = 1639026083952861927L;
    private static JPanel contentPane;
    private final transient MainView login;
    private MainController controller;

    /**
     * Create the frame.
     * @param main 
     */
    public MainView(MainController main) {
        this.controller = main;
        new JPanel();
        login = this;
        

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 334, 150);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 100, 5));
        setContentPane(contentPane);
        this.setLocationRelativeTo(null);
        final JButton btnNewButton = new JButton("Play");
        btnNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                controller.pressPlayButton();
            }
        });

        final JButton btnQuitter = new JButton("Quit");
        btnQuitter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {

                System.exit(0);

            }
        });
        
        final JButton btnOpt = new JButton("Settings");
        btnOpt.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                new Thread("Options")
                {
                    @Override
                    public void run()
                    {
                        OptionVIew opt = new OptionVIew(controller);
                        opt.setVisible(true);
                    }
                }.start();
            }
        });
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.LINE_AXIS));
        panel2.add(btnNewButton);
        panel2.add(btnOpt);
        panel2.add(btnQuitter);
        contentPane.add(panel2);

    }


    @Override
    public void modelPropertyChange(final PropertyChangeEvent evt)
    {
        if (evt.getPropertyName().equals(MainController.ELEMENT_BUTTON_PLAY))
        {
            final RemotePack[] packs = (RemotePack[]) evt.getNewValue();
            if(packs.length == 1)
                controller.selectModPack(packs[0]);
            else
            {
                new Thread("View")
                {
                    @Override
                    public void run()
                    {
                        ModPackSelectView pack = new ModPackSelectView(login,
                                packs, controller);
                        pack.setVisible(true);
                    }
                }.start();
            }
           
            
        } else if (evt.getPropertyName().equals(MainController.PACK))
        {
            // logButt.setEnabled(false);
            Thread t = new Thread("Login")
            {
                @Override
                public void run()
                {
                    try
                    {
                        controller.manageStart();
                    } catch (InterruptedException e)
                    {
                        
                        e.printStackTrace();
                    }
                }
            };
            t.start();
        }
        
        else if (evt.getPropertyName().equals("DISPLAY_ERROR"))
            JOptionPane.showMessageDialog(this, evt.getNewValue(),
                    Constants.LAUNCHER_NAME + " -  Error",
                    JOptionPane.ERROR_MESSAGE);
        else if (evt.getPropertyName().equals("MASK"))
            this.setVisible(false);
    }


}
