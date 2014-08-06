package fr.wherecraft.launcher.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import fr.wherecraft.launcher.controller.MainController;
import fr.wherecraft.launcher.model.RemotePack;

public class ModPackSelectView extends JDialog implements IView
{
    
    private static final long serialVersionUID = -8997865757545196081L;
    private final JPanel contentPanel = new JPanel();
    
    
    /**
     * Create the dialog.
     * @param mainView 
     */
    public ModPackSelectView(JFrame mainView, RemotePack[] packs, final MainController controller)
    {
        super(mainView);
        final ModPackSelectView instance = this;
        setBounds(100, 100, 580, 100);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new GridLayout(0, 2, 0, 0));
            JLabel lblNewLabel = new JLabel("Select a Pack");
            contentPanel.add(lblNewLabel);
            final JComboBox<RemotePack> comboBox = new JComboBox<RemotePack>();
            for(RemotePack pack : packs)
                comboBox.addItem(pack);
            
            
            contentPanel.add(comboBox);
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.setActionCommand("OK");
                okButton.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        instance.setVisible(false);
                        controller.selectModPack((RemotePack)comboBox.getSelectedItem());
                    }
                    
                });
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
        }


    @Override
    public void modelPropertyChange(PropertyChangeEvent evt)
    {
        // TODO Auto-generated method stub
        
    }
    
}

