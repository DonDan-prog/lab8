import java.util.Set;

public class WebCrawler 
{
    private static int numThreads = 5;
    public static void setNumThreads(int newNumThreads) { numThreads = newNumThreads; }

    private URLPool pool;
    /** The specified start url pair */
    private URLPair urlStart;
    /** The specified maxDepth */
    private int maxDepth;

    private CrawlerTaskHandler taskHandler;

    WebCrawler(String urlString, int maxDepth) throws Exception
    {
        this.urlStart = new URLPair(urlString, 0);
        this.maxDepth = maxDepth;

        this.pool = new URLPool();

        this.taskHandler = new CrawlerTaskHandler(numThreads);
    }
    /** Method for crawl the whole site */
    public void crawlSite()
    {
        /** Init the logger */
        WorkLogger.log("WebCrawler start at site " + this.urlStart.getFullUrl() + " and maxDepth = " + this.maxDepth);
        WorkLogger.log("====== START ======");
        WorkLogger.log("");
        /** Save the start time of crawling */
        long startTime = System.currentTimeMillis();
        /** Add the first page to crawl - it's the start url itself 
         *  It MUSTN'T throw exception
        */
        try { this.pool.addToQueue(this.urlStart); } catch (Exception e) {}
        for(int i = 0; i <= this.maxDepth && this.pool.isEmpty(); i++)
        {
            /** Log new step */
            WorkLogger.log("===== DEPTH " + i + " ======");
            /** Get the remain length of queued pages, than run exactly this amount of times the crawlOne */
            int remainLen = this.pool.getQueueSize();
            this.taskHandler.runNumTasks(pool, i + 1, remainLen);
            /** Add extra skip in log file */
            WorkLogger.log("");
        }
        /** End the log file and add the time elapsed and total sites visited */
        WorkLogger.log("====== END ======");
        WorkLogger.log("Time elapsed: " + (System.currentTimeMillis() - startTime)/1000.);
        WorkLogger.log("Total visited sites: " + this.pool.getVisitedSize());
    }
    /** Method that returns the set of sites to print them all in console */
    public Set<URLPair> getVisited() { return this.pool.getVisitedKeys(); }
}