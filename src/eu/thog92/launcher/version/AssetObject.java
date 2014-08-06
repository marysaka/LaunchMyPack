package eu.thog92.launcher.version;


public class AssetObject {
    private String hash;
    private long size;

    public String getHash() {
        return this.hash;
    }

    public long getSize() {
        return this.size;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if ((o == null) || (getClass() != o.getClass()))
            return false;

        AssetObject that = (AssetObject) o;

        if (this.size != that.size)
            return false;
        return this.hash.equals(that.hash);
    }

    public int hashCode() {
        int result = this.hash.hashCode();
        result = 31 * result + (int) (this.size ^ this.size >>> 32);
        return result;
    }
}