package eu.thog92.launcher.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import eu.thog92.launcher.launch.JavaProcess;
import eu.thog92.launcher.launch.JavaProcessRunnable;

public class ProcessMonitorThread extends Thread
{
    private final JavaProcess process;
    
    public ProcessMonitorThread(JavaProcess process)
    {
        this.process = process;
    }
    
    public void run()
    {
        InputStreamReader reader = new InputStreamReader(this.process
                .getRawProcess().getInputStream());
        BufferedReader buf = new BufferedReader(reader);
        String line = null;
        
        while (this.process.isRunning())
        {
            try
            {
                while ((line = buf.readLine()) != null)
                {
                    System.out.println("Client> " + line);
                    
                    this.process.getSysOutLines().add(line);
                }
            } catch (IOException ex)
            {
                return;
            } finally
            {
                try
                {
                    reader.close();
                } catch (IOException e)
                {
                    
                }
            }
        }
        
        JavaProcessRunnable onExit = this.process.getExitRunnable();
        
        if (onExit != null)
            onExit.onJavaProcessEnded(this.process);
    }
}
