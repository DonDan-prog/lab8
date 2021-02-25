public class CrawlerTaskHandler 
{   
    private final int numThreads;
    private final CrawlTask[] workres;
    private final URLPool pool;

    CrawlerTaskHandler(URLPool pool, int numThreads)
    {
        this.pool = pool;
        this.numThreads = numThreads;
        this.workres = new CrawlTask[numThreads];
    }
    public void startTask(URLPair startUrl, Integer maxDepth)
    {
        /** Add the first page to crawl - it's the start URL itself 
         *  It MUSTN'T throw exception
        */
        try { this.pool.addToQueue(startUrl); } catch (Exception e) {}
        /** Run the workers */
        for(int i = 0; i < this.numThreads; i++)
        {
            this.workres[i] = new CrawlTask(this.pool, maxDepth);
            this.workres[i].start();
        }
    }
    public boolean isEnd()
    {
        int waiting = 0;
        for(int i = 0; i < numThreads; i++)
        {
            if(this.workres[i].isWaiting() == true)
                waiting++;
        }
        return waiting == this.numThreads;
    }
}
