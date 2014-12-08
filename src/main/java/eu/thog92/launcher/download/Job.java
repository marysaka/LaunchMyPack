package eu.thog92.launcher.download;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import eu.thog92.launcher.demoimpl.view.DownloadIView;

/**
 *
 * @author Thomas
 */
public class Job {

    private final static ThreadPoolExecutor EXECUTORSERVICE = new ExceptionalThreadPoolExecutor(50);
    private final transient List<Downloadable> filetodownload;
    //private ArrayList<FileDownload> remainingFiles;
    private transient boolean finished;
    private DownloadIView view;
    public Job(final List<Downloadable> list, DownloadIView v) {
        this.filetodownload = list;
        this.view = v;
        this.finished = false;
    }
    
    public Job(final Set<Downloadable> set, DownloadIView v)
    {
        this.filetodownload = new ArrayList<Downloadable>();
        this.filetodownload.addAll(set);
        this.view = v;
        this.finished = false;
    }

    public boolean isFinish() {
        return finished;
    }
     public boolean isAssetsFinish() {
        return this.remainingFiles.isEmpty();
    }
    private final transient AtomicInteger remainingThreads = new AtomicInteger();
    private final transient Queue<Downloadable> remainingFiles = new ConcurrentLinkedQueue<Downloadable>();

    public void startDownload() {
        view.getProgressBar().setStringPainted(true);
        Downloadable task;
        if(this.filetodownload.isEmpty())
        {
            view.setInfo("Download job " + Thread.currentThread().getName() + " skipped as there are no files to download");
        }
        float maxSize = this.filetodownload.size();
        for (float i = 0; i < maxSize; i++) 
        {
            view.setProgressTxt((int)i + " / " + (int)maxSize + " files");
            task = this.filetodownload.get((int)i);
            task.setView(view);
            try
            {
                System.out.println(task.download());
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            
            float result = (i/maxSize) * 100;
            view.setProgressValue((int) (result));
        }
        view.getProgressBar().setStringPainted(false);
        this.finished = true;
    }

    public void startDownloadAssets() {
        this.remainingFiles.addAll(this.filetodownload);
        if (this.remainingFiles.isEmpty()) {

            System.out.println("Download job ModPack skipped as there are no files to download");
            this.finished = true;
        } else {
            final int threads = EXECUTORSERVICE.getMaximumPoolSize();
            this.remainingThreads.set(threads);
            System.out.println("Download job ModPack started (" + threads + " threads, " + this.remainingFiles.size() + " files)");
            for (int i = 0; i < threads; i++) {
                EXECUTORSERVICE.submit(new Runnable() {
                    @Override
                    public void run() {
                        Downloadable task;
                        while  ((task = remainingFiles.poll()) != null) {
                            try {
                            task.setView(view);
                            task.download();
                //log.logInfo(downloadable.getTarget().getName() + " " + result);
                            //Statut.setInfo(downloadable.getTarget().getName() + " " + result);
                            //this.successful.add(downloadable);
                            
                            remainingFiles.remove(task);
                            if(remainingFiles.isEmpty())
                            {
                                finished = true;
                            }

                        } catch (Throwable t) {
                            t.printStackTrace();
                            remainingFiles.add(task);
                        }
                            
                           // continue;
                        }
                        
                        finished = true;
                    }
                });

            }
            
        }
    }
}
