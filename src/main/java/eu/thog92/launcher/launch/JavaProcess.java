package eu.thog92.launcher.launch;

import eu.thog92.launcher.controller.ProcessMonitorThread;

import java.util.List;

public class JavaProcess
{
    private final List<String> commands;
    private final Process process;
    private final LimitedCapacityList<String> sysOutLines = new LimitedCapacityList(String.class, 5);
    private JavaProcessRunnable onExit;
    private ProcessMonitorThread monitor;

    public JavaProcess(List<String> commands, Process process)
    {
        this.commands = commands;
        this.process = process;

        if (System.console() != null || true)
        {
            this.monitor = new ProcessMonitorThread(this);
            this.monitor.start();
        }
    }

    public Process getRawProcess()
    {
        return this.process;
    }

    public List<String> getStartupCommands()
    {
        return this.commands;
    }

    public String getStartupCommand()
    {
        return this.process.toString();
    }

    public LimitedCapacityList<String> getSysOutLines()
    {
        return this.sysOutLines;
    }

    public boolean isRunning()
    {
        try
        {
            this.process.exitValue();
        } catch (IllegalThreadStateException ex)
        {
            return true;
        }

        return false;
    }

    public void safeSetExitRunnable(JavaProcessRunnable runnable)
    {
        setExitRunnable(runnable);

        try
        {
            process.waitFor();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        if (runnable != null)
            runnable.onJavaProcessEnded(this);
    }

    public JavaProcessRunnable getExitRunnable()
    {
        return this.onExit;
    }

    public void setExitRunnable(JavaProcessRunnable runnable)
    {
        this.onExit = runnable;
    }

    public int getExitCode()
    {
        try
        {
            return this.process.exitValue();
        } catch (IllegalThreadStateException ex)
        {
            ex.fillInStackTrace();
        }

        return 1;
    }

    @Override
    public String toString()
    {
        return "JavaProcess[commands=" + this.commands + ", isRunning="
                + isRunning() + "]";
    }
}