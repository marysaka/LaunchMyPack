package eu.thog92.launcher.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter
{
    private final LogAgent agent;
    private SimpleDateFormat dataformat;

    protected LogFormatter(LogAgent par1LogAgent)
    {
        this.agent = par1LogAgent;
        this.dataformat = new SimpleDateFormat("[HH:mm:ss]");
    }

    @Override
    public String format(LogRecord par1LogRecord)
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(this.dataformat.format(par1LogRecord
                .getMillis()));

        if (LogAgent.getPrefix(this.agent) != null)
        {
            stringbuilder.append(LogAgent.getPrefix(this.agent));
        }

        stringbuilder.append(" [").append(par1LogRecord.getLevel().getName())
                .append("] ");
        stringbuilder.append(this.formatMessage(par1LogRecord));
        stringbuilder.append('\n');
        Throwable throwable = par1LogRecord.getThrown();

        if (throwable != null)
        {
            StringWriter stringwriter = new StringWriter();
            throwable.printStackTrace(new PrintWriter(stringwriter));
            stringbuilder.append(stringwriter.toString());
        }

        return stringbuilder.toString();
    }

}
