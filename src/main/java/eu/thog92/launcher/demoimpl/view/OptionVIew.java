package eu.thog92.launcher.demoimpl.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JComboBox;

import eu.thog92.launcher.demoimpl.controller.MainController;
import eu.thog92.launcher.demoimpl.model.LaunchModel;

public class OptionVIew extends JDialog
{
    
    private final JPanel contentPanel = new JPanel();
    
    public OptionVIew(final MainController controller)
    {
        final JDialog dial = this;
        final LaunchModel model = controller.getLaunchModel();
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setLayout(new FlowLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        
            JLabel lblRam = new JLabel("RAM");
            contentPanel.add(lblRam);
        
        
            final JComboBox<Integer> comboBox = new JComboBox<Integer>();
            comboBox.addItem(512);
            for(int i = 1; i < 9; i++)
            {
                comboBox.addItem(1024 * i);
            }
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
                        model.setRAM((Integer)comboBox.getSelectedItem());
                        controller.savePreference();
                        dial.setVisible(false);
                        dial.dispose();
                    }
                    
                });
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            
            
                JButton cancelButton = new JButton("Cancel");
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
                
            
        }
    }
    
}
