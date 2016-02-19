package eu.thog92.launcher.version;

import eu.thog92.launcher.util.Constants;
import eu.thog92.launcher.util.OperatingSystem;

import java.util.*;

public class Library
{
    private String name;
    private List<CompatibilityRule> rules;
    private Map<OperatingSystem, String> natives;
    private ExtractRules extract;
    private String url;

    public Library()
    {
    }

    public Library(String name)
    {
        if ((name == null) || (name.length() == 0))
        {
            throw new IllegalArgumentException("Library name cannot be null or empty");
        }
        this.name = name;
    }

    public Library(Library library)
    {
        this.name = library.name;
        this.url = library.url;
        if (library.extract != null)
        {
            this.extract = new ExtractRules(library.extract);
        }
        if (library.rules != null)
        {
            this.rules = new ArrayList();
            for (CompatibilityRule compatibilityRule : library.rules)
            {
                this.rules.add(new CompatibilityRule(compatibilityRule));
            }
        }
        if (library.natives != null)
        {
            this.natives = new LinkedHashMap();
            for (Map.Entry<OperatingSystem, String> entry : library.getNatives().entrySet())
            {
                this.natives.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public String getName()
    {
        return this.name;
    }

    public Library addNative(OperatingSystem operatingSystem, String name)
    {
        if ((operatingSystem == null) || (!operatingSystem.isSupported()))
        {
            throw new IllegalArgumentException("Cannot add native for unsupported OS");
        }
        if ((name == null) || (name.length() == 0))
        {
            throw new IllegalArgumentException("Cannot add native for null or empty name");
        }
        if (this.natives == null)
        {
            this.natives = new EnumMap(OperatingSystem.class);
        }
        this.natives.put(operatingSystem, name);
        return this;
    }

    public List<CompatibilityRule> getCompatibilityRules()
    {
        return this.rules;
    }

    public boolean appliesToCurrentEnvironment()
    {
        if (this.rules == null)
        {
            return true;
        }
        CompatibilityRule.Action lastAction = CompatibilityRule.Action.DISALLOW;
        for (CompatibilityRule compatibilityRule : this.rules)
        {
            CompatibilityRule.Action action = compatibilityRule.getAppliedAction();
            if (action != null)
            {
                lastAction = action;
            }
        }
        return lastAction == CompatibilityRule.Action.ALLOW;
    }

    public Map<OperatingSystem, String> getNatives()
    {
        return this.natives;
    }

    public ExtractRules getExtractRules()
    {
        return this.extract;
    }

    public Library setExtractRules(ExtractRules rules)
    {
        this.extract = rules;
        return this;
    }

    public String getArtifactBaseDir()
    {
        if (this.name == null)
        {
            throw new IllegalStateException("Cannot get artifact dir of empty/blank artifact");
        }
        String[] parts = this.name.split(":", 4);
        return String.format("%s/%s/%s", parts[0].replaceAll("\\.", "/"), parts[1], parts[2]);
    }

    public String getArtifactPath()
    {
        return getArtifactPath(null);
    }

    public String getArtifactPath(String classifier)
    {
        if (this.name == null)
        {
            throw new IllegalStateException("Cannot get artifact path of empty/blank artifact");
        }
        if (classifier == null)
        {
            String[] parts = this.name.split(":");
            if (parts.length == 4)
                classifier = parts[3];
        }
        return String.format("%s/%s", getArtifactBaseDir(), getArtifactFilename(classifier));
    }

    public String getArtifactFilename(String classifier)
    {
        if (classifier == null) classifier = "";
        else classifier = "-" + classifier;
        classifier = classifier.replace("${arch}", System.getProperty("sun.arch.data.model"));
        if (this.name == null)
        {
            throw new IllegalStateException("Cannot get artifact filename of empty/blank artifact");
        }
        String[] parts = this.name.split(":", 4);
        String result = String.format("%s-%s%s.jar", parts[1], parts[2], classifier);

        return result;
    }

    @Override
    public String toString()
    {
        return "Library{name='" + this.name + '\'' + ", rules=" + this.rules + ", natives=" + this.natives + ", extract=" + this.extract + '}';
    }

    public boolean hasCustomUrl()
    {
        return this.url != null;
    }

    public String getDownloadUrl()
    {
        if (this.url != null)
        {
            return this.url;
        }
        return Constants.getLibBase();
    }
}
