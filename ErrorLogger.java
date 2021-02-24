import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ErrorLogger 
{
    /** Descripto of log file */
    private static FileWriter log = null;
    /** This is the formatter for data */
    private static SimpleDateFormat formatter = new SimpleDateFormat("[yyy-MM-dd 'at' HH:mm::ss] ");
    /** Queue for writing */
    private static ConcurrentLinkedQueue<String> writeQueue;
    /** Init the file with name */
    public static void init(String logName) 
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
    /** The method for logging the given string with line separator */
    public static synchronized void log(String toWrite) 
    {
        if(log == null) return;

        String date = formatter.format(new Date(System.currentTimeMillis()));
        writeQueue.add(date + toWrite + "\r\n");
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
    /** The method to close the file */
    public static void close()
    {
        if(log == null) return;
        try { log.close(); } 
        catch (Exception e) { }
    }
}