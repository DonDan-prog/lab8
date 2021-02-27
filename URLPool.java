import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class URLPool 
{
    /** Collections for keep track of visited pages and qeued pages */
    private final HashSet<URLPair> queuePairs;
    private final HashSet<URLPair> visitedSet;

    /** Init collections and add the start url to queue list */
    URLPool(int numThreads)
    {
        this.queuePairs = new HashSet<URLPair>(5000);
        this.visitedSet = new HashSet<URLPair>(5000);
    }
    /** Some methods to work */
    public int getVisitedSize() { return this.visitedSet.size(); }
    public Set<URLPair> getVisitedKeys() { return this.visitedSet; }
    /** Method to add the URL in the queue; it able to add only if this URL was never been visited yet */
    public synchronized boolean addToQueue(URLPair urlPair) throws Exception
    {
        if(queuePairs.contains(urlPair) == true)
            throw new Exception("already queued");
        queuePairs.add(urlPair);
        /** Notify all threads that thay able to get a task */
        notify();
        return true;
    }
    /** Adding URL to visited; no need to check because we made sure that this URL unvisited earlier */
    public synchronized void addVisited(URLPair urlPair) throws Exception
    {
        if(visitedSet.contains(urlPair) == true)
            throw new Exception("already visited");
        visitedSet.add(urlPair);

        //System.gc();
        System.out.println("Queue size: " + queuePairs.size());
        System.out.println("Visited size: " + visitedSet.size());
    }
    /** Getting the first from the queue */
    public synchronized URLPair poll()
    {
        Iterator<URLPair> it = this.queuePairs.iterator();
        URLPair ret = it.next();
        try
        {
            this.queuePairs.remove(ret);
        } catch(Exception e) { System.out.println("Error: " + e); }
        
        return ret;
    }
}