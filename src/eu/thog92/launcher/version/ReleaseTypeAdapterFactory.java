package eu.thog92.launcher.version;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ReleaseTypeAdapterFactory<T extends ReleaseType> extends
        TypeAdapter<T>
{
    
    public ReleaseTypeAdapterFactory()
    {
    }
    
    public void write(JsonWriter out, T value) throws IOException
    {
        out.value(value.getName());
    }
    
    @SuppressWarnings("unchecked")
    public T read(JsonReader in) throws IOException
    {
        return (T) ReleaseType.getByName(in.nextString());
    }
}
