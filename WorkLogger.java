import java.io.FileWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

/** The logger to write the url's that was visited */
public class WorkLogger
{
    /** Log file descriptor */
    private static FileWriter log = null;
    /** Queue for writing */
    private static ConcurrentLinkedQueue<String> writeQueue;

    /** Initialize with given name */
    static void init(String logName) 
    {
        try 
        {
            log = new FileWriter(logName);
            writeQueue = new ConcurrentLinkedQueue<String>();
        } 
        catch (Exception e) 
        {
            log = null;
            writeQueue = null;
        }
    }
    /** Log the given string */
    public static synchronized void log(String toWrite) 
    {
        if (log == null) return;
        writeQueue.add(toWrite + "\r\n");
    }
    /** Perfom writing */
    public static void write()
    {
        if(log == null) return;
        try 
        {
            int remainLen = writeQueue.size();
            for(int i = 0; i < remainLen; i++)
                log.write(writeQueue.poll());
            log.flush();
        } 
        catch (Exception e)
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