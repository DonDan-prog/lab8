public class CrawlTask extends Thread
{
    private URLPool pool;
    private boolean waiting;
    private final int maxDepth;
    /** The method to crawl the one page */
    CrawlTask(URLPool pool, int maxDepth)
    {
        this.pool = pool;
        this.waiting = true;
        this.maxDepth = maxDepth;
    }
    public boolean isWaiting() { return this.waiting; }
    @Override
    public void run()
    {
        while(true)
        {
            try 
            {
                /** The flag for our checker */
                this.waiting = true;
                /** We wait while our worker not recieve tasks */
                while(this.pool.isEmpty() == true) { Thread.sleep(1000); }
                /** Recieve task */
                URLPair urlPair = this.pool.poll();
                /** If something happend and we got null, just skip it */
                if(urlPair == null) continue;
                /** If depth of gotten task is more, than maxDepth, then the program shall complete; break the cycle and this will terminate the worker */
                if(urlPair.getDepth() > this.maxDepth)
                    break;
                /** The flag set to checker that worker is working now */
                this.waiting = false;
                /** If not - add to visited */
                pool.addVisited(urlPair);
                System.out.println("Checking " + urlPair);
                /** Add to log that site is visiting */
                WorkLogger.log(urlPair.toString());
                /** Make new request */
                Request request = new Request(urlPair);
                HtmlParser parser = new HtmlParser(this.pool, urlPair, urlPair.getDepth());
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
                    parser.parse(request.getBufferedReader());
                }
                else if(statusCode == 301)
                {
                    urlPair = new URLPair(responce.getParameter("Location:"), urlPair.getDepth());
                    request.close();

                    request = new Request(urlPair);
                    responce = new HTTPResponce(request.getBufferedReader());

                    if(responce.getStatusCode() == 200)
                        parser.parse(request.getBufferedReader());
                }
                /** Close the request as we already done all we can do with it */
                request.close();
            }
            catch (Exception e) 
            {
                /** Log the error */
                ErrorLogger.log("WebCrawler::WebCrawler: " + e);
            }
        }
    }
}