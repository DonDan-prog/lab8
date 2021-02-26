import java.util.Set;

public class WebCrawler 
{
    /** Variable to know how much workers will perform crawling */
    private static int numThreads = 5;
    /** Static method to set amount of workers */
    public static void setNumThreads(int newNumThreads) throws Exception 
    { 
        /** In case of incorrect input */
        if(newNumThreads < 0)
            throw new Exception("attepmt to create negative count of threads");
        numThreads = newNumThreads; 
    }

    /** Pool of URL pairs; includes queue and visited collections */
    private URLPool pool;
    /** The specified start url pair */
    private URLPair urlStart;
    /** The specified maxDepth */
    private int maxDepth;
    /** Task handler for multithread the crawling */
    private CrawlerTaskHandler taskHandler;

    WebCrawler(String urlString, int maxDepth) throws Exception
    {
        this.urlStart = new URLPair(urlString, 0);
        this.maxDepth = maxDepth;

        this.pool = new URLPool(numThreads);

        this.taskHandler = new CrawlerTaskHandler(this.pool, numThreads);
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
        /** Start our workers */
        this.taskHandler.startTask(this.urlStart, maxDepth);
        /** Check workers finished their work; in case then all workers wait, then no ones work, that leads no more tasks -> the program finished */
        while(this.taskHandler.isEnd() == false)
        {
            /** Default timeout for 1 sec cause the socket timeout I set to 1 sec */
            try{Thread.sleep(1000);}catch(Exception e){}
        }
        this.taskHandler.stopTasks();
        /** End the log file and add the time elapsed and total sites visited */
        WorkLogger.log("====== END ======");
        WorkLogger.log("Time elapsed: " + (System.currentTimeMillis() - startTime)/1000.);
        WorkLogger.log("Total visited sites: " + this.pool.getVisitedSize());
    }
    /** Method that returns the set of sites to print them all in console */
    public Set<URLPair> getVisited() { return this.pool.getVisitedKeys(); }
}