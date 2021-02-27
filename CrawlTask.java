import java.util.NoSuchElementException;

public class CrawlTask extends Thread
{
    private final URLPool pool;
    private boolean waiting;
    private final RegexHtmlParser parser;
    /** The method to crawl the one page */
    CrawlTask(final URLPool pool, final Integer maxDepth)
    {
        this.pool = pool;
        this.waiting = true;
        this.parser = new RegexHtmlParser(this.pool, maxDepth);
    }
    public boolean isWaiting() { return this.waiting; }
    @Override
    public void run()
    {
        while(!isInterrupted())
        {
            URLPair urlPair = null;
            try 
            {
                /** The flag for our checker */
                this.waiting = true;
                /** Recieve task */
                urlPair = this.pool.poll();
                /** If something happend and we got null, just skip it */
                if(urlPair == null)
                {
                    wait();
                    continue;
                }
                /** The flag set to checker that worker is working now */
                this.waiting = false;

                pool.addVisited(urlPair);
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
                    parser.parse(request.getBufferedReader(), urlPair, urlPair.getDepth());
                }
                else if(statusCode == 301)
                {
                    urlPair = new URLPair(responce.getParameter("Location:"), urlPair.getDepth());
                    request.close();

                    request = new Request(urlPair);
                    responce = new HTTPResponce(request.getBufferedReader());
                    if(responce.getStatusCode() == 200)
                        parser.parse(request.getBufferedReader(), urlPair, urlPair.getDepth());
                }
                else
                    ErrorLogger.log("Page " + urlPair + " status code " + statusCode);
                /** Close the request as we already done all we can do with it */
                request.close();
            }
            catch(NoSuchElementException e)
            {
                continue;
            }
            catch(IllegalMonitorStateException e) {}
            catch (Exception e) 
            {
                /** Log the error */
                ErrorLogger.log("CrawlerTask::run: " + e);
            }
        }
    }
}