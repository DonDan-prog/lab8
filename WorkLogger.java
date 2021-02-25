import java.io.FileWriter;

/** The logger to write the url's that was visited */
public class WorkLogger
{
    /** Log file descriptor */
    private static FileWriter log = null;

    /** Initialize with given name */
    static void init(String logName) 
    {
        try 
        {
            log = new FileWriter(logName);
        } 
        catch (Exception e) 
        {
            log = null;
        }
    }
    /** Log the given string */
    public static synchronized void log(String toWrite) 
    {
        if(log == null) return;
        try
        {
            log.write(toWrite);
            log.write("\r\n");
            log.flush();
        }
        catch(Exception e)
        {
            ErrorLogger.log("WorkLogger::write: " + e);
        }
    }
    /** Close the file */
    public static void close()
    {
        if(log == null) return;
        try { log.close(); } 
        catch (Exception e) { ErrorLogger.log("WorkLogger::close: " + e); }
    }
}