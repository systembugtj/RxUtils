package me.systembug.reactivex.event;

/**
 * Created by systembug on 4/1/16.
 */
public class AsyncEvent {
    public EventType eventType;
    public Object eventDetail;

    public AsyncEvent(EventType type) {
        this.eventType = type;
    }
    public AsyncEvent(EventType type, Object eventDetail) {
        this.eventType = type;
        this.eventDetail = eventDetail;
    }
}
