public class CrawlerTaskHandler
{
    private int numThreads;
    private CrawlTask[] tasks;
    private int loadedTasks;

    CrawlerTaskHandler(int numThreads)
    {
        this.numThreads = numThreads;
        this.loadedTasks = 0;
        this.tasks = new CrawlTask[this.numThreads];
    }

    public void runNumTasks(URLPool pool, int depth, int amountTasks)
    {
        for(int i = 0; i < amountTasks; i += numThreads)
        {
            int limit = (i + numThreads < amountTasks ? numThreads : amountTasks - i);
            this.loadedTasks = limit;

            for(int j = 0; j < this.loadedTasks; j++)
            {
                this.tasks[j] = new CrawlTask(pool, depth);
                this.tasks[j].start();
            }
            while(this.isTasksComplete() == false) { try{ Thread.sleep(100);}catch(Exception e){}}
        }
    }

    private boolean isTasksComplete()
    {
        for(int i = 0; i < this.loadedTasks; i++)
        {
            if(this.tasks[i].isAlive() == true)
                return false;
        }
        return true;
    }
}