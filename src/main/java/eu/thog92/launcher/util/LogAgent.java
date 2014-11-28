package eu.thog92.launcher.util;

import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LogAgent
{
    private Logger clientLogger;
    private final File logFile;
    private final String loggerName;
    private final String loggerPrefix;
    private static LogAgent INSTANCE;
    
    private LogAgent(String name, String prefix, File file, File file1)
    {
        INSTANCE = this;
        this.clientLogger = Logger.getLogger(name);
        this.loggerName = name;
        this.loggerPrefix = prefix;
        this.logFile = file;
        if (file1.exists())
        {
            file1.delete();
        }
        if (file.exists())
        {
            // boolean isrename = file.renameTo(file1);
        }
        
        this.setupLogger();
        
    }
    
    /**
     * Sets up the logger for usage.
     */
    private void setupLogger()
    {
        LogManager.getLogManager().reset();
        Handler[] ahandler = this.clientLogger.getHandlers();
        int i = ahandler.length;
        
        for (int j = 0; j < i; ++j)
        {
            Handler handler = ahandler[j];
            this.clientLogger.removeHandler(handler);
        }
        
        LogFormatter logformatter = new LogFormatter(this);
        
        try
        {
            
            ConsoleHandler ch = new ConsoleHandler();
            ch.setFormatter(logformatter);
            this.clientLogger.addHandler(ch);
            this.logFile.getParentFile().mkdirs();
            this.logFile.createNewFile();
            if (this.logFile != null)
            {
                FileHandler filehandler = new FileHandler(
                        logFile.getAbsolutePath(), true);
                filehandler.setFormatter(logformatter);
                this.clientLogger.addHandler(filehandler);
            }
            
        } catch (Exception exception)
        {
            this.clientLogger.log(Level.WARNING, "Failed to log "
                    + this.loggerName + " to " + this.logFile, exception);
        }
    }
    
    public void logInfo(String par1Str)
    {
        this.clientLogger.log(Level.INFO, par1Str);
    }
    
    public void logWarning(String par1Str)
    {
        this.clientLogger.log(Level.WARNING, par1Str);
    }
    
    public void logWarningFormatted(String par1Str, Object... par2ArrayOfObj)
    {
        this.clientLogger.log(Level.WARNING, par1Str, par2ArrayOfObj);
    }
    
    public void logWarningException(String par1Str, Throwable par2Throwable)
    {
        this.clientLogger.log(Level.WARNING, par1Str, par2Throwable);
    }
    
    public void logSevere(String par1Str)
    {
        this.clientLogger.log(Level.SEVERE, par1Str);
    }
    
    public void logSevereException(String par1Str, Throwable par2Throwable)
    {
        this.clientLogger.log(Level.SEVERE, par1Str, par2Throwable);
    }
    
    public void logFine(String par1Str)
    {
        this.clientLogger.log(Level.FINE, par1Str);
    }
    
    public void logDebug(String par1Str)
    {
        this.clientLogger.log(Level.FINEST, par1Str);
    }
    
    static String getPrefix(LogAgent par0LogAgent)
    {
        return par0LogAgent.loggerPrefix;
    }

    public static LogAgent getLogAgent()
    {
        if(INSTANCE == null) new LogAgent(Constants.LAUNCHER_NAME, "Launcher", new File(Util.getWorkingDirectory() + "/launcher.log"), new File(Util.getWorkingDirectory() + "/launcher_1.log"));
        // TODO Auto-generated method stub
        return INSTANCE;
    }
}
