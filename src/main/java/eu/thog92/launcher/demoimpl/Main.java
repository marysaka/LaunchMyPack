package eu.thog92.launcher.demoimpl;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import eu.thog92.launcher.demoimpl.controller.MainController;
import eu.thog92.launcher.demoimpl.view.MainView;

public class Main
{
    public static void main(final String[] args)
    {
        
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    final MainController main = new MainController();
                    main.preInit();
                    Thread graphics = new Thread("Graphics") {
                        @Override
                        public void run() {
                            final MainView view = new MainView(main);
                            main.addView(view);
                            view.setVisible(true);
                        }
                        
                    };
                    graphics.start();
                    
                }
                
            });
        } catch (ClassNotFoundException e)
        {
            
            e.printStackTrace();
        } catch (InstantiationException e)
        {
            
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e)
        {
            
            e.printStackTrace();
        }
}
}