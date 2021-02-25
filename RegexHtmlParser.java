import java.io.BufferedReader;
import java.util.regex.*;

public class RegexHtmlParser 
{
    private final String A_HREF_STRING = "<a[^>]+href=\"(.+?)\"";
    private final int MAX_LEN = 200;

    private URLPool pool;
    private URLPair urlPair;
    private Integer depth;

    RegexHtmlParser(URLPool pool, URLPair urlPair, Integer depth)
    {
        this.pool = pool;
        this.urlPair = urlPair;
        this.depth = depth;
    }
    public void parse(BufferedReader in) throws Exception
    {
        long startTime = System.currentTimeMillis();
        String line = "";

        /** We'll read the whole page anyway, so using the thread-unsafe builder will be most efficient */
        Pattern patternPage = Pattern.compile(A_HREF_STRING);
        StringBuilder builder = new StringBuilder(MAX_LEN);
        while((line = in.readLine()) != null)
        {
            builder.insert(0, line);
            builder.setLength(line.length());
            Matcher matcherAtag = patternPage.matcher(builder);
            if(matcherAtag.find() == true)
            {
                String url = builder.substring(builder.indexOf("href=\"", matcherAtag.start()) + 6, matcherAtag.end() - 1);

                /** If the  */
                if(url.startsWith("#")) continue;
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
        }
        System.out.println(String.format("Time %f for parsing page %s", (System.currentTimeMillis() - startTime)/1000., this.urlPair));
    }

}
