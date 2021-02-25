import java.io.BufferedReader;
import java.util.HashMap;

public final class HTTPResponce 
{
    /** Field for keep the status code of current responce */
    private int statusCode;
    /** The map for keep the parameters of responce */
    private HashMap<String, String> values;

    public HTTPResponce(BufferedReader in) throws Exception
    {
        this.values = new HashMap<String, String>();
        /** Read the first line, in HTTP it MUST contain status code */
        String line = in.readLine();
        statusCode = Integer.parseInt(line.split(" ")[1]);
        /** Read the rest lines of responce */
        while((line = in.readLine()).equals("") == false)
        {
            /** String builder must be faster than string, idk */
            StringBuilder tempLine = new StringBuilder(line);
            /** Find the separator between key and parameter name */
            int parameterSeparator = tempLine.indexOf(":");
            String key = tempLine.substring(0, parameterSeparator);
            /** Two steps from the index of separator will give us skip the ": " */
            String value = tempLine.substring(parameterSeparator + 2);
            values.put(key, value);
        }
    }
    /** Return the status code of current responce */
    public int getStatusCode() { return this.statusCode; }
    /** Get parameter from values map */
    public String getParameter(final String key)
    {
        return this.values.get(key);
    }
}