package eu.thog92.launcher.version;

import eu.thog92.launcher.util.OperatingSystem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rule
{
    private final Action action = Action.ALLOW;
    private OSRestriction os;

    public Action getAppliedAction()
    {
        if (os != null && !os.isCurrentOperatingSystem())
            return null;

        return action;
    }

    @Override
    public String toString()
    {
        return "Rule{action=" + action + ", os=" + os + '}';
    }

    public enum Action
    {
        ALLOW, DISALLOW
    }

    public class OSRestriction
    {
        private OperatingSystem name;
        private String version;

        public OSRestriction()
        {
        }

        public boolean isCurrentOperatingSystem()
        {
            if (name != null && name != OperatingSystem.getCurrentPlatform())
                return false;

            if (version != null)
                try
                {
                    final Pattern pattern = Pattern.compile(version);
                    final Matcher matcher = pattern.matcher(System.getProperty("os.version"));
                    if (!matcher.matches())
                        return false;
                } catch (final Throwable localThrowable)
                {
                }
            return true;
        }

        @Override
        public String toString()
        {
            return "OSRestriction{name=" + name + ", version='" + version + '\'' + '}';
        }
    }
}