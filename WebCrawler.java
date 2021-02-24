import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WebCrawler 
{
    /** Queue of remaining sites to crawl */
    private ConcurrentLinkedQueue<URLPair> queue;
    /** The map of visited; used Map instead of Set because I'll port this program into multithread */
    private HashMap<URLPair, Integer> map;
    /** The specified start url pair */
    private URLPair urlStart;
    /** The specified maxDepth */
    private int maxDepth;
    /** Is the crawler was created */
    private boolean isCreated;
    /** Checker for threads */
    private volatile int[] checker;

    WebCrawler(String urlString, int maxDepth)
    {
        /** We try to initilaze all the class variables;
         *  If something failed, we null everything and set the isCreated to false
         */
        try 
        {
            this.urlStart = new URLPair(urlString, 0);
            this.maxDepth = maxDepth;

            this.queue = new ConcurrentLinkedQueue<URLPair>();
            this.map = new HashMap<URLPair, Integer>();

            this.checker = new int[1];

            this.isCreated = true;
        }
        catch (Exception e) 
        {
            this.queue = null;
            this.map = null;
            this.urlStart = null;
            this.maxDepth = 0;
            this.checker = null;

            this.isCreated = false;

            ErrorLogger.log("WebCrawler::WebCrawler: " + e);
        }
    }
    /** Method for crawl the whole site */
    public void crawlSite()
    {
        /** If not created just return */
        if(this.isCreated == false) return;
        /** Init the logger */
        WorkLogger.log("WebCrawler start at site " + this.urlStart.getFullUrl() + " and maxDepth = " + this.maxDepth);
        WorkLogger.log("====== START ======");
        WorkLogger.log("");
        /** Save the start time of crawling */
        long startTime = System.currentTimeMillis();
        /** Add the first page to crawl - it's the start url itself */
        this.queue.add(this.urlStart);
        for(int i = 0; i < this.maxDepth && this.queue.size() > 0; i++)
        {
            /** Log new step */
            WorkLogger.log("===== DEPTH " + i + " ======");
            /** Get the remain length of queued pages, than run exactly this amount of times the crawlOne */
            int remainLen = this.queue.size();
            /** Checker for keep track of amount of completed threads */
            this.checker[0] = 0;
            for(int j = 0; j < remainLen; j++)
            {
                Thread thread = new Thread(new CrawlTask(i + 1));
                thread.start();
            }
            /** While not all threads completed */
            while(true)
            {
                if(this.checker[0] == remainLen) 
                    break;
                try { Thread.sleep(1000); } 
                catch (Exception e) { }
            }
            WorkLogger.write();
            ErrorLogger.write();
            /** Add extra skip in log file */
            WorkLogger.log("");
        }
        /** End the log file and add the time elapsed and total sites visited */
        WorkLogger.log("====== END ======");
        WorkLogger.log("Time elapsed: " + (System.currentTimeMillis() - startTime)/100.);
        WorkLogger.log("Total visited sites: " + this.map.size());

        WorkLogger.write();
        ErrorLogger.write();
    }
    /** Method that returns the set of sites to print them all in console */
    public Set<URLPair> getVisited() { return map.keySet(); }
    private synchronized void incrementChecker() { this.checker[0]++; }

    private class CrawlTask implements Runnable
    {
        private int currentDepth;
        /** The method to crawl the one page */
        CrawlTask(int depth)
        {
            this.currentDepth = depth;
        }
        public void run()
        {
            try 
            {
                /** Extract the crawling page from the queue */
                URLPair urlPair = queue.poll();
                /** Check if this site already visited */
                if(map.containsKey(urlPair) == true) 
                    throw new Exception("already visited");
                /** If not - add to visited */
                map.put(urlPair, 0);
                /** Add to log that site is visiting */
                WorkLogger.log(urlPair.toString());
                /** Make new request */
                Request request = new Request(urlPair);
                /** Send GET request */
                request.sendGET();
                /** Parse the responce */
                HTTPResponce responce = new HTTPResponce(request.getBufferedReader());
                /** Get the status code of responce */
                int statusCode = responce.getStatusCode();
                /** Check for responces that we can handle with;
                 *  Status code 200 - OK, parse the responce
                 *  Status code 301 - redirect, we can handle it
                 */
                if(statusCode == 200)
                {
                    if(responce.getParameter("Content-Type").indexOf("text/html") == -1)
                        throw new Exception("wrong type of content");
                    HtmlParser.parse(request.getBufferedReader(), queue, urlPair, currentDepth);
                }
                else if(statusCode == 301)
                    queue.add(new URLPair(responce.getParameter("Location:"), currentDepth));
                /** Close the request as we already done all we can with it */
                request.close();
            }
            catch (Exception e) 
            {
                /** Log the error */
                ErrorLogger.log("WebCrawler::WebCrawler: " + e);
            }
            incrementChecker();
        }
    }
}