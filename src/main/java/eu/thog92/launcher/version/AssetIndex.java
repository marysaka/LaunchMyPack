package eu.thog92.launcher.version;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import eu.thog92.launcher.download.Downloadable;
import eu.thog92.launcher.download.FileDownload;
import eu.thog92.launcher.util.IOUtils;
import eu.thog92.launcher.util.StringUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URL;
import java.util.*;

public class AssetIndex
{
    public static final String DEFAULT_ASSET_NAME = "legacy";
    private Map<String, AssetObject> objects;
    private boolean virtual;

    public AssetIndex()
    {
        this.objects = new LinkedHashMap<String, AssetObject>();
    }

    public static ArrayList<Downloadable> getResourceFiles(String indexName, File workDir)
    {
        ArrayList<Downloadable> downloadassets = new ArrayList<Downloadable>();
        Proxy proxy = Proxy.NO_PROXY;
        InputStream inputStream = null;
        File assets = new File(workDir,
                "assets");
        assets.mkdir();
        File objectsFolder = new File(assets, "objects");
        objectsFolder.mkdir();
        File indexesFolder = new File(assets, "indexes");
        indexesFolder.mkdir();
        if (indexName == null)
        {
            indexName = "legacy";
        }
        File indexFile = new File(indexesFolder, indexName + ".json");
        indexFile.delete();

        try
        {
            URL indexUrl = new URL(
                    "https://s3.amazonaws.com/Minecraft.Download/indexes/"
                            + indexName + ".json");
            inputStream = indexUrl.openConnection(proxy)
                    .getInputStream();
            String json = StringUtils.toString(inputStream, "UTF-8");

            StringUtils.writeStringToFile(indexFile, json, "UTF-8");
            AssetIndex index = new GsonBuilder().create().fromJson(json, AssetIndex.class);
            for (AssetObject object : index
                    .getObjects())
            {

                String filename = object.getHash().substring(0, 2)
                        + "/" + object.getHash();

                File file = new File(objectsFolder, filename);
                if ((!file.isFile())
                        || (file.length() != object.getSize()))
                {
                    FileDownload downloadable = new FileDownload(new URL(
                            "http://resources.download.minecraft.net/"
                                    + filename), file,
                            object.getSize());

                    downloadassets.add(downloadable);

                }

            }

        } catch (IOException ex)
        {

        } catch (JsonSyntaxException ex)
        {

        } finally
        {
            IOUtils.closeQuietly(inputStream);
        }

        return downloadassets;

    }

    public Map<String, AssetObject> getObjectMap()
    {
        return this.objects;
    }

    public Set<AssetObject> getObjects()
    {
        return new HashSet<AssetObject>(this.objects.values());
    }

    public boolean isVirtual()
    {
        return this.virtual;
    }

}
