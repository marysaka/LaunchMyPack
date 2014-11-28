package eu.thog92.launcher.launch;

import java.lang.reflect.Array;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LimitedCapacityList<T>
{
    private final T[] items;
    private final Class<? extends T> clazz;
    private final ReadWriteLock locks = new ReentrantReadWriteLock();
    private int size;
    private int head;
    
    public LimitedCapacityList(Class<? extends T> clazz, int maxSize)
    {
        this.clazz = clazz;
        this.items = (T[]) Array.newInstance(clazz, maxSize);
    }
    
    public T add(T value)
    {
        this.locks.writeLock().lock();
        
        this.items[this.head] = value;
        this.head = ((this.head + 1) % getMaxSize());
        if (this.size < getMaxSize())
            this.size += 1;
        
        this.locks.writeLock().unlock();
        return value;
    }
    
    public int getSize()
    {
        this.locks.readLock().lock();
        int result = this.size;
        this.locks.readLock().unlock();
        return result;
    }
    
    public int getMaxSize()
    {
        this.locks.readLock().lock();
        int result = this.items.length;
        this.locks.readLock().unlock();
        return result;
    }
    
    public T[] getItems()
    {
        final T[] result = (T[]) Array.newInstance(clazz, size);
        
        locks.readLock().lock();
        for (int i = 0; i < size; i++)
        {
            int pos = (head - size + i) % getMaxSize();
            if (pos < 0)
                pos += getMaxSize();
            result[i] = items[pos];
        }
        locks.readLock().unlock();
        
        return result;
    }
}