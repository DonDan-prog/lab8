import java.util.LinkedList;
import java.util.HashMap;
import java.util.Set;

public final class URLPool 
{
    /** Collections for keep track of visited pages and qeued pages */
    private final LinkedList<URLPair> queuePairs;
    private final HashMap<URLPair, Integer> visitedMap;

    /** Init collections and add the start url to queue list */
    URLPool(int numThreads)
    {
        this.queuePairs = new LinkedList<URLPair>();
        this.visitedMap = new HashMap<URLPair, Integer>();
    }
    /** Some methods to work */
    public int getQueueSize() { return queuePairs.size(); }
    public int getVisitedSize() { return visitedMap.size(); }
    public synchronized boolean isEmpty() { return queuePairs.size() < 0; }
    public Set<URLPair> getVisitedKeys() { return visitedMap.keySet(); }
    /** Method to add the URL in the queue; it able to add only if this URL was never been visited yet */
    public synchronized boolean addToQueue(URLPair urlPair) throws Exception
    {
        if(visitedMap.containsKey(urlPair) == true)
            throw new Exception("already in visited");
        queuePairs.addLast(urlPair);
        /** Notify all threads that thay able to get a task */
        notify();
        return true;
    }
    /** Adding URL to visited; no need to check because we made sure that this URL unvisited earlier */
    public synchronized void addVisited(URLPair urlPair)
    {
        visitedMap.put(urlPair, 0);
    }
    /** Getting the first from the queue */
    public synchronized URLPair poll()
    {
        return queuePairs.pollFirst();
    }
}