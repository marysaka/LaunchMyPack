package eu.thog92.launcher.view;

/**
 * Desc...
 * Created by Thog the 19/02/2016
 */
public interface IDownloadView
{
    String getStatus();
    String getInfo();
    void setInfo(String info);
    void setStatut(String status);

    void setIndeterminate(boolean isIndeterminate);

    void setStringPainted(boolean painted);

    void setProgressValue(int value);

    void setProgressTxt(String txt);
}
