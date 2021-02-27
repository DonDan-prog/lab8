public class URLPair 
{
    /** The full url string for comparison and everything */
    private String fullUrlString;
    /** Protocol string, e.g "http", "https" */
    private String protocolString;
    /** The host string to connect and request */
    private String hostString;
    /** The path string to request */
    private String pathString;
    /** The query string; not using now, don't know for what */
    private String queryString;
    /** The port of URLPair */
    private Integer port;
    /** The current depth; using for crawler */
    private Integer depth;

    private int hash;

    /** We need to handle the hidden protocol, hidden domen and relative URL in parser */
    URLPair() 
    {
        this.protocolString = null;
        this.protocolString = null;
        this.fullUrlString = null;
        this.hostString = null;
        this.pathString = null;
        this.queryString = null;
        this.port = 0;
        this.depth = 0;
        this.hash = 0;
    }
    /** The constructor for the pair of url and depth */
    URLPair(String url, final Integer depth) throws Exception
    {
        /** Check for valid protocol */
        if(url == null) throw new Exception("URLPair::URLPair: null url get");
        if(url.startsWith("http") == false)
            throw new Exception("URLPair::URLPair: unknown protocol");
        else if(url.startsWith("https://") == true)
            throw new Exception("URLPair::URLPair: unsupported protocol");
        /** Check for valid end of url */
        if(url.endsWith(".jpg"))
            throw new Exception("URLPair::URLPair: jpg file get");
        else if(url.endsWith(".png"))
            throw new Exception("URLPair::URLPair: png file get");
        else if(url.endsWith(".mp4"))
            throw new Exception("URLPair::URLPair: mp4 file get");
        else if(url.endsWith(".pdf"))
            throw new Exception("URLPair::URLPair: pdf file get");
        /** Making StringBuilder out of String because it may be faster, idk */
        StringBuilder urlBuilder = new StringBuilder(url);
        parseUrl(urlBuilder);
        this.depth = depth;
        this.hash = this.fullUrlString.hashCode();
    }
    /** Hidden method for parse url 
     *  Complicated one
    */
    private void parseUrl(StringBuilder url) throws Exception
    {
        /** Get protocol */
        int protocolEnd = url.indexOf("://");
        if(protocolEnd == -1) throw new Exception("URLPair::parseUrl: undefined protocol");
        this.protocolString = url.substring(0, protocolEnd);
        /** Get the port; if didn't find - ok */
        int portEnd = url.indexOf(":", protocolEnd + 3);
        /** Find the end of host or/and port */
        int hostEnd = url.indexOf("/", protocolEnd + 3);
        /** It means that there's no query and other */
        if(hostEnd == -1)
        {   
            /** If URL specified port */
            if(portEnd != -1)
            {
                this.port = Integer.parseInt(url.substring(portEnd + 1));
                this.hostString = url.substring(protocolEnd + 3, portEnd);
            }
            else
            {
                this.port = 80;
                this.hostString = url.substring(protocolEnd + 3);
            }
            this.pathString = "/";
            this.fullUrlString = this.protocolString + "://" + this.hostString + this.pathString;
            return;
        }
        /** Find thing that anchor starts with #:, so we need to take care of not having troubles with it */
        if(portEnd > hostEnd) portEnd = -1;
        /** It means that URL has host end separator and haven't port end */
        if(portEnd == -1)
        {
            this.port = 80;
            this.hostString = url.substring(protocolEnd + 3, hostEnd);
        }
        else
        {
            this.port = Integer.parseInt(url.substring(portEnd + 1, hostEnd));
            this.hostString = url.substring(protocolEnd + 3, portEnd);
        }
        /** If there's a query, extract path, then query; otherwise extract path and return */
        int pathEnd = url.indexOf("?");
        if(pathEnd == -1)
        {
            int anchorPos = url.indexOf("#", hostEnd);
            if(anchorPos == -1) this.pathString = url.substring(hostEnd);
            else this.pathString = url.substring(hostEnd, anchorPos);
            this.fullUrlString = this.protocolString + "://" + this.hostString + this.pathString;
            return;
        }
        this.pathString = url.substring(hostEnd, pathEnd);
        /** If there's an anchor, we need to ignore it and extract query string */
        int anchorPos = url.indexOf("#", pathEnd);
        if(anchorPos == -1) this.queryString = url.substring(pathEnd + 1);
        else this.queryString = url.substring(pathEnd + 1, anchorPos);

        this.fullUrlString = this.protocolString + "://" + this.hostString + this.pathString + '?' + this.queryString;
    }
    /** Getters of URL fields */
    public String getFullUrl() { return this.fullUrlString; }
    public String getProtocol() { return this.protocolString; }
    public String getHost() { return this.hostString; }
    public String getPath() { return this.pathString; }
    public String getQuery() { return this.queryString; }
    
    public Integer getDepth() { return this.depth; }
    public Integer getPort() { return this.port; }

    /** Methods to work with Java collections and to print normally */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
		if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        URLPair other = (URLPair)obj;
        return this.fullUrlString.equals(other.fullUrlString); 

    }
    public int hashCode() { return this.hash; }

    /** The function speaks for itself */
    public String toString()
    {
        return String.format("[URL=%s, depth=%d]", this.fullUrlString, this.depth);
    }
}
