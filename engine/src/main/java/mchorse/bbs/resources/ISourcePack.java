package mchorse.bbs.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ISourcePack
{
    public String getPrefix();

    public boolean hasAsset(Link link);

    public InputStream getAsset(Link link) throws IOException;

    public File getFile(Link link);

    public void getLinksFromPath(List<Link> links, Link link, boolean recursive);
}