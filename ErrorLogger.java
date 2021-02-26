import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ErrorLogger 
{
    /** Descriptor of log file */
    private static FileWriter log = null;
    /** This is the formatter for data */
    private static SimpleDateFormat formatter = new SimpleDateFormat("[yyy-MM-dd 'at' HH:mm:ss] ");
    /** Init the file with name */
    public static void init(String logName) 
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
    /** The method for logging the given string with line separator */
    public static synchronized void log(String toWrite) 
    {
        if(log == null) return;
        try
        {
            String date = formatter.format(new Date(System.currentTimeMillis()));
            log.write(date);
            log.write(toWrite);
            log.write("\r\n");
            log.flush();
        }
        catch(Exception e)
        {
            System.out.println("ErrorLogger::log: " + e);
        }
    }
    /** The method to close the file */
    public static void close()
    {
        if(log == null) return;
        try { log.close(); log = null; } 
        catch (Exception e) { }
    }
}