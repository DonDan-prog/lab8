import java.util.LinkedList;
import java.util.HashMap;
import java.util.Set;

public final class URLPool 
{
    private final LinkedList<URLPair> queuePairs = new LinkedList<URLPair>();
    private final HashMap<URLPair, Integer> visitedMap = new HashMap<URLPair, Integer>();

    public int getQueueSize() { return queuePairs.size(); }
    public int getVisitedSize() { return visitedMap.size(); }
    public boolean isEmpty() { return queuePairs.size() > 0; }
    public Set<URLPair> getVisitedKeys() { return visitedMap.keySet(); }

    public synchronized boolean addToQueue(URLPair urlPair) throws Exception
    {
        if(visitedMap.containsKey(urlPair) == true)
            throw new Exception("already in visited");
        queuePairs.addLast(urlPair);
        return true;
    }
    public synchronized void addVisited(URLPair urlPair)
    {
        visitedMap.put(urlPair, 0);
    }
    public synchronized URLPair poll()
    {
        return queuePairs.pollFirst();
    }
}