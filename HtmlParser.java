import java.io.BufferedReader;

public class HtmlParser 
{
    /** Constant prefixes */
    private static final String A_PREFIX = "<a";
    private static final String HREF_PREFIX = "href=\"";

    private URLPool pool;
    private URLPair urlPair;
    private Integer depth;

    HtmlParser(URLPool pool, URLPair urlPair, Integer depth)

    {
        this.pool = pool;
        this.urlPair = urlPair;
        this.depth = depth;
    }
    /** Perfoming parse on the fly of inputted data */
    public void parse(BufferedReader in) throws Exception
    {
        long startTime = System.currentTimeMillis();
        String line = "";

        /** We'll read the whole page anyway, so using the thread-unsafe builder will be most efficient */
        StringBuilder builder = new StringBuilder();
        while((line = in.readLine()) != null)
            builder.append(line);
        /** Search all of <a tags using hand parse */
        int pos = 0;
        while(pos < builder.length())
        {
            int aTagStart = builder.indexOf(A_PREFIX, pos);
            if(aTagStart == -1) { pos++; continue; }
            pos = aTagStart;
            int hrefStart = builder.indexOf(HREF_PREFIX, pos);
            if(hrefStart == -1) { pos++; continue; }
            pos = hrefStart;
            int hrefEnd = builder.indexOf("\"", pos + HREF_PREFIX.length());
            if(hrefEnd == -1) { pos++; continue; }

            String url = builder.substring(pos + HREF_PREFIX.length(), hrefEnd);
            pos = hrefEnd;
            if(url.startsWith("#")) { pos++; continue; }

            /** This means that URL with hidden protocol */
            if(url.startsWith("//")) url = urlPair.getProtocol() + "://" + url;
            /** This means that URL with hidden domen */
            if(url.startsWith("/")) url = urlPair.getFullUrl() + url.substring(1);
            try
            {
                pool.addToQueue(new URLPair(url, this.depth + 1));

            }
            catch(Exception e)
            {
                ErrorLogger.log("WebCrawler::WebCrawler: " + e + " caused from " + url);
            }
        }
        System.out.println(String.format("Time %f for parsing page %s", (System.currentTimeMillis() - startTime)/1000., this.urlPair));
    }
}