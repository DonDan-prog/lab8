import java.io.BufferedReader;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HtmlParser 
{
    /** Constant prefixes */
    private static final String APREFIX = "<a";
    private static final String HREFPREFIX = "href=\"";
    /** Perfoming parse on the fly of inputted data */
    public static void parse(BufferedReader in, ConcurrentLinkedQueue<URLPair> queue, URLPair urlPair, int depth) throws Exception
    {
        String line = "";
        while((line = in.readLine()) != null)
        {
            int aTagStart = line.indexOf(APREFIX);
            if(aTagStart == -1) continue;
            int hrefStart = line.indexOf(HREFPREFIX, aTagStart);
            if(hrefStart == -1) continue;
            int hrefEnd = line.indexOf("\"", hrefStart + HREFPREFIX.length());
            if(hrefEnd == -1) continue;

            String url = line.substring(hrefStart + HREFPREFIX.length(), hrefEnd);
            if(url.startsWith("#")) continue;
            /** This means that URL with hidden protocol */
            if(url.startsWith("//")) url = urlPair.getProtocol() + "://" + url;
            /** This means that URL with hidden domen */
            if(url.startsWith("/")) url = urlPair.getFullUrl() + url.substring(1);

            try
            {
                queue.add(new URLPair(url, depth));
            }
            catch(Exception e)
            {
                ErrorLogger.log("WebCrawler::WebCrawler: " + e + " caused from " + url);
            }
        }
    }
}