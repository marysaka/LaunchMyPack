package eu.thog92.launcher.version;


public class AssetObject
{
    private String hash;
    private long size;

    public String getHash()
    {
        return this.hash;
    }

    public long getSize()
    {
        return this.size;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if ((o == null) || (getClass() != o.getClass()))
            return false;

        AssetObject that = (AssetObject) o;

        return this.size == that.size && this.hash.equals(that.hash);
    }

    @Override
    public int hashCode()
    {
        return 31 * this.hash.hashCode() + (int) (this.size ^ this.size >>> 32);
    }
}