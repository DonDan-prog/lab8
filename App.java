import ArgumentParser.*;

public class App
{
    public static void main(String[] args) 
    {
        /** Whenether the input, first init the error logger */
        final String errorLogName = "errors.txt";
        ErrorLogger.init(errorLogName);

        /** Creating holders for command promt arguments */
        StringHolder urlHolder = new StringHolder();
        IntHolder depthHolder = new IntHolder();

        IntHolder flagThreadsHolder = new IntHolder();
        StringHolder flagLogNameHolder = new StringHolder();

        ArgumentParser parser = new ArgumentParser("App");
        try
        {
            /** Adding positional and thus required arguments to parser */
            parser.addPositional("the url to crawl", "url", urlHolder);
            parser.addPositional("the max depth of crawler", "depth", depthHolder);
            /** Adding the non positional and non required arguments */
            parser.addArgument("-t %d the num of threads to crawl", "threads", flagThreadsHolder, false);
            parser.addArgument("-l %s the name of log file", "logName", flagLogNameHolder, false);
            /** Parse the command promt arguments */
            parser.parse(args);

            if(flagThreadsHolder.isEmpty() == false) WebCrawler.setNumThreads(flagThreadsHolder.getResource());
            if(flagLogNameHolder.isEmpty() == false) WorkLogger.init(flagLogNameHolder.getResource());
            else WorkLogger.init("log.txt");
            
            WebCrawler crawler = new WebCrawler(urlHolder.getResource(), depthHolder.getResource());
            crawler.crawlSite();

            for(URLPair i : crawler.getVisited())
                System.out.println(i);
        } 
        catch (Exception e)
        {
            ErrorLogger.log(e.toString());
            System.out.println("Probably You mistyped the input. Check the " + errorLogName + " for more information");
            System.out.println(parser);
        }

        WorkLogger.close();
        ErrorLogger.close();
    }
}