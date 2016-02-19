package eu.thog92.launcher.download;

import java.util.concurrent.*;


public class ExceptionalThreadPoolExecutor extends ThreadPoolExecutor
{
    public ExceptionalThreadPoolExecutor(final int threadCount)
    {
        super(threadCount, threadCount, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void afterExecute(final Runnable r, Throwable t)
    {
        super.afterExecute(r, t);

        if (t == null && r instanceof Future)
            try
            {
                final Future<Runnable> future = (Future<Runnable>) r;
                if (future.isDone())
                    future.get();
            } catch (final CancellationException ce)
            {
                t = ce;
            } catch (final ExecutionException ee)
            {
                t = ee.getCause();
            } catch (final InterruptedException ie)
            {
                Thread.currentThread().interrupt();
            }
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(final Callable<T> callable)
    {
        return new ExceptionalFutureTask<T>(callable);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(final Runnable runnable, final T value)
    {
        return new ExceptionalFutureTask<T>(runnable, value);
    }

    public class ExceptionalFutureTask<T> extends FutureTask<T>
    {

        public ExceptionalFutureTask(final Callable<T> callable)
        {
            super(callable);
        }

        public ExceptionalFutureTask(final Runnable runnable, final T value)
        {
            super(runnable, value);
        }

        @Override
        protected void done()
        {
            try
            {
                get();
            } catch (final Throwable t)
            {
                t.printStackTrace();
            }
        }
    }
}