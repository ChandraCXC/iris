package cfa.vo.iris.utils;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class Metrics {
    
    private final String id;
    private final long start;
    private final List<Metrics> childMetrics;
    
    private long time = -1;
    private boolean closed = false;
    
    public Metrics(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException("metrics id cannot be null");
        }
        
        this.id = id;
        this.start = System.currentTimeMillis();
        this.childMetrics = new LinkedList<>();
    }
    
    public void close() {
        if (closed) return;
        for (Metrics child : childMetrics) {
            child.close();
        }
        
        time = System.currentTimeMillis() - start;
    }
    
    boolean isClosed() {
        return closed;
    }
    
    public Metrics openMetrics(String subId) {
        Metrics child = new Metrics(String.format("%s.%s", id, subId));
        childMetrics.add(child);
        return child;
    }
    
    public long getTime() {
        return time;
    }
    
    public String report() {
        this.close();
        StringBuilder builder = new StringBuilder();
        builder.append(id + " : " + time);
        for (Metrics child : childMetrics) {
            builder.append("\n" + child.report());
        }
        return builder.toString();
    }
}
