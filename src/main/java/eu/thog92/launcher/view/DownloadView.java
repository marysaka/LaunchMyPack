package eu.thog92.launcher.view;

import eu.thog92.launcher.view.IDownloadView;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DownloadView extends JFrame implements Runnable, IDownloadView
{

    private static final long serialVersionUID = -3521539622682742964L;
    private static String infotxt;
    private final JPanel contentPanel = new JPanel();
    private JProgressBar progressBar;
    private JLabel statut = new JLabel();
    private String statuttxt;
    private JLabel label;

    /**
     * Create the dialog.
     */
    public DownloadView()
    {
    }

    @Override
    public String getStatus()
    {
        return null;
    }

    public String getInfo()
    {
        return infotxt;
    }

    public void setInfo(String newinfo)
    {
        infotxt = newinfo;
        label.setText(newinfo);
    }

    public String getStatut()
    {
        return infotxt;
    }

    public void setStatut(String newstatut)
    {
        if (statut != null)
        {
            statuttxt = newstatut;
            statut.setText(statuttxt);
        }

    }

    @Override
    public void setIndeterminate(boolean isIndeterminate)
    {
        this.progressBar.setIndeterminate(isIndeterminate);
    }

    @Override
    public void setStringPainted(boolean painted)
    {
        this.progressBar.setStringPainted(painted);
    }

    public void setProgressValue(int value)
    {
        this.progressBar.setValue(value);
    }

    @Override
    public void run()
    {
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent evt)
            {
                onExit();
            }

            public void onExit()
            {
                System.err.println("Exit");
                System.exit(0);
            }
        });

        infotxt = "Loading...";
        setBounds(100, 100, 600, 146);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        progressBar = new JProgressBar(0, 100);
        label = new JLabel(infotxt);

        GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
        gl_contentPanel.setHorizontalGroup(gl_contentPanel.createParallelGroup(
                Alignment.LEADING).addGroup(
                gl_contentPanel
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                                gl_contentPanel
                                        .createParallelGroup(Alignment.LEADING)
                                        .addComponent(progressBar,
                                                GroupLayout.DEFAULT_SIZE, 554,
                                                Short.MAX_VALUE)
                                        .addComponent(label)
                                        .addComponent(statut))
                        .addContainerGap()));
        gl_contentPanel.setVerticalGroup(gl_contentPanel.createParallelGroup(
                Alignment.TRAILING).addGroup(
                gl_contentPanel
                        .createSequentialGroup()
                        .addContainerGap()
                        .addComponent(statut)
                        .addPreferredGap(ComponentPlacement.RELATED, 15,
                                Short.MAX_VALUE)
                        .addComponent(label)
                        .addGap(18)
                        .addComponent(progressBar, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE).addContainerGap()));
        contentPanel.setLayout(gl_contentPanel);
    }

    public void finish()
    {

    }

    public void startProgress()
    {

    }

    public JProgressBar getProgressBar()
    {
        return progressBar;
    }

    public void setProgressTxt(String string)
    {
        progressBar.setString(string);

    }
}
