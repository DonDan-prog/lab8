import java.io.BufferedReader;

public class HtmlParser 
{
    /** Constant prefixes */
    private static final String A_PREFIX = "<a";
    private static final String HREF_PREFIX = "href=\"";

    private URLPool pool;
    private URLPair urlPair;
    private int depth;

    HtmlParser(URLPool pool, URLPair urlPair, int depth)
    {
        this.pool = pool;
        this.urlPair = urlPair;
        this.depth = depth;
    }
    /** Perfoming parse on the fly of inputted data */
    public void parse(BufferedReader in) throws Exception
    {
        String line = "";
        while((line = in.readLine()) != null)
        {
            int aTagStart = line.indexOf(A_PREFIX);
            if(aTagStart == -1) continue;
            int hrefStart = line.indexOf(HREF_PREFIX, aTagStart);
            if(hrefStart == -1) continue;
            int hrefEnd = line.indexOf("\"", hrefStart + HREF_PREFIX.length());
            if(hrefEnd == -1) continue;

            String url = line.substring(hrefStart + HREF_PREFIX.length(), hrefEnd);
            if(url.startsWith("#")) continue;
            /** This means that URL with hidden protocol */
            if(url.startsWith("//")) url = urlPair.getProtocol() + "://" + url;
            /** This means that URL with hidden domen */
            if(url.startsWith("/")) url = urlPair.getFullUrl() + url.substring(1);

            try
            {
                pool.addToQueue(new URLPair(url, depth));
            }
            catch(Exception e)
            {
                ErrorLogger.log("WebCrawler::WebCrawler: " + e + " caused from " + url);
            }
        }
    }
}