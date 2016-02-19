package eu.thog92.launcher.version;

import java.util.Date;

public interface IVersion
{
    String getId();

    ReleaseType getType();

    void setType(ReleaseType paramReleaseType);

    Date getUpdatedTime();

    void setUpdatedTime(Date paramDate);

    Date getReleaseTime();

    void setReleaseTime(Date paramDate);
}
