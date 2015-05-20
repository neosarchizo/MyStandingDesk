package kr.neosarchizo.mystandingdesk;

/**
 * Created by JunhyukLee on 15. 5. 20..
 */
public class BTServiceEvent {

    public enum Event {
        NONE,
        DATA_READ,
        CONNECTED,
        CONNECTING,
        CONNECTION_FAIL,
        CONNECTION_LOST
    }

    public enum State {
        STATE_NONE,
        STATE_CONNECTING,
        STATE_CONNECTED
    }

    private Event mEvent;
    private State mState;
    private byte[] mBuffer;
    private int mBufferSize;

    public BTServiceEvent(Event event) {
        mEvent = event;
    }

    public BTServiceEvent(Event event,  byte[] buffer, int bufferSize) {
        mEvent = event;
        mBuffer = buffer;
        mBufferSize = bufferSize;
    }

    public Event getEvent() {
        return mEvent;
    }

    public State getState() {
        return mState;
    }

    public byte[] getBuffer() {
        return mBuffer;
    }

    public int getBufferSize() {
        return mBufferSize;
    }
}
