import java.util.Set;

public class WebCrawler 
{
    /** Variable to know how much workers will perform crawling */
    private static int numThreads = 5;
    /** Static method to set amount of workers */
    public static void setNumThreads(int newNumThreads) { numThreads = newNumThreads; }

    private URLPool pool;
    /** The specified start url pair */
    private URLPair urlStart;
    /** The specified maxDepth */
    private int maxDepth;

    WebCrawler(String urlString, int maxDepth) throws Exception
    {
        this.urlStart = new URLPair(urlString, 0);
        this.maxDepth = maxDepth;

        this.pool = new URLPool(numThreads, this.urlStart);
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
        /** TODO: incapsulate CrawlerTaskHandler */
        /** Run our workers */
        CrawlTask[] tasks = new CrawlTask[numThreads];
        for(int i = 0; i < numThreads; i++)
        {
            tasks[i] = new CrawlTask(pool, maxDepth);
            tasks[i].start();
        }
        /** Check if they waiting for another task; in case then all workers wait, then no ones work, that leads no more tasks -> the program finished */
        boolean isEnd = false;
        while(!isEnd)
        {
            int waiting = 0;
            for(int i = 0; i < numThreads; i++)
            {
                if(tasks[i].isWaiting() == true)
                    waiting++;
            }
            if(waiting == numThreads) break;
            /** Default timeout for 1 sec cause the socket timeout I set to 1 sec */
            try{Thread.sleep(1000);}catch(Exception e){}
        }
        /** End the log file and add the time elapsed and total sites visited */
        WorkLogger.log("====== END ======");
        WorkLogger.log("Time elapsed: " + (System.currentTimeMillis() - startTime)/1000.);
        WorkLogger.log("Total visited sites: " + this.pool.getVisitedSize());
    }
    /** Method that returns the set of sites to print them all in console */
    public Set<URLPair> getVisited() { return this.pool.getVisitedKeys(); }
}