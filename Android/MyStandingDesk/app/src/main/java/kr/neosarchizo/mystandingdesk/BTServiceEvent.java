package kr.neosarchizo.mystandingdesk;

/**
 * Created by JunhyukLee on 15. 5. 20..
 */
public class BTServiceEvent {

    public enum Event {
        NONE,
        CONNECTED,
        CONNECTING,
        CONNECTION_FAIL,
        CONNECTION_LOST,
        DISTANCE,
        STATE
    }

    private Event mEvent;
    private int mValue;

    public BTServiceEvent(Event event) {
        mEvent = event;
    }

    public BTServiceEvent(Event event, int value) {
        mEvent = event;
        mValue = value;
    }

    public Event getEvent() {
        return mEvent;
    }

    public int getValue() {
        return mValue;
    }
}
