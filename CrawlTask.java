public class CrawlTask extends Thread
{
    private int currentDepth;
    private URLPool pool;
    /** The method to crawl the one page */
    CrawlTask(URLPool pool, int depth)
    {
        this.pool = pool;
        this.currentDepth = depth;
    }
    @Override
    public void run()
    {
        try 
        {
            /** Extract the crawling page from the queue */
            URLPair urlPair = this.pool.poll();
            /** If not - add to visited */
            pool.addVisited(urlPair);
            /** Add to log that site is visiting */
            WorkLogger.log(urlPair.toString());
            /** Make new request */
            Request request = new Request(urlPair);
            HtmlParser parser = new HtmlParser(this.pool, urlPair, currentDepth);
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
                urlPair = new URLPair(responce.getParameter("Location:"), currentDepth);
                request.close();

                request = new Request(urlPair);
                responce = new HTTPResponce(request.getBufferedReader());

                if(responce.getStatusCode() == 200)
                    parser.parse(request.getBufferedReader());
            }
            /** Close the request as we already done all we can with it */
            request.close();
        }
        catch (Exception e) 
        {
            /** Log the error */
            ErrorLogger.log("WebCrawler::WebCrawler: " + e);
        }
    }
}