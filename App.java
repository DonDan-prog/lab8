public class App
{
    public static void main(String[] args) 
    {
        /** Whenether the input, first init the error logger */
        final String errorLogName = "error.txt";
        ErrorLogger.init(errorLogName);
        /** Check if we specify equal or more than 2 arguments; if less - that's the wrong usage */
        if(args.length > 1)
        {
            if(args.length == 3) WorkLogger.init(args[2]);
            else WorkLogger.init("log.txt");

            try 
            {
                WebCrawler crawler = new WebCrawler(args[0], Integer.parseInt(args[1]));
                crawler.crawlSite();

                for(URLPair i : crawler.getVisited())
                    System.out.println(i);
            } 
            catch (Exception e)
            {
                ErrorLogger.log(e.toString());
                System.out.println("Probably You mistyped the input. Check the " + errorLogName + " for more information");
                System.out.println("Usage: java App.java <full url> <maxDepth> [log name]");
            }

            WorkLogger.close();
        }
        else
        {
            ErrorLogger.log("Error in launcging program: didn't use command args");
            System.out.println("Usage: java App.java <full url> <maxDepth> [log name]");
        }
        ErrorLogger.close();
    }
}