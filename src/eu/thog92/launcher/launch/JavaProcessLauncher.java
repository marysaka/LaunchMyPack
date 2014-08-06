package eu.thog92.launcher.launch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.thog92.launcher.util.OperatingSystem;

public class JavaProcessLauncher
{
    private final String jvmPath;
    private final List<String> commands;
    private File directory;
    
    public JavaProcessLauncher(String jvmPath, String[] commands)
    {
        if (jvmPath == null)
            jvmPath = OperatingSystem.getCurrentPlatform().getJavaDir();
        this.jvmPath = jvmPath;
        this.commands = new ArrayList<String>(commands.length);
        addCommands(commands);
    }
    
    public JavaProcess start() throws IOException
    {
        List<String> full = getFullCommands();
        return new JavaProcess(full, new ProcessBuilder(full)
                .directory(this.directory).redirectErrorStream(true).start());
    }
    
    public List<String> getFullCommands()
    {
        List<String> result = new ArrayList<String>(this.commands);
        result.add(0, getJavaPath());
        return result;
    }
    
    public List<String> getCommands()
    {
        /* 35 */return this.commands;
    }
    
    public void addCommands(String[] commands)
    {
        for (String s : commands)
        {
            System.out.println(s);
        }
        
        this.commands.addAll(Arrays.asList(commands));
    }
    
    public void addSplitCommands(String commands)
    {
        addCommands(commands.split(" "));
    }
    
    public JavaProcessLauncher directory(File directory)
    {
        this.directory = directory;
        
        return this;
    }
    
    public File getDirectory()
    {
        return this.directory;
    }
    
    protected String getJavaPath()
    {
        return this.jvmPath;
    }
    
    public String toString()
    {
        return "JavaProcessLauncher[commands=" + this.commands + ", java="
                + this.jvmPath + "]";
    }
}