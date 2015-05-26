package kr.neosarchizo.mystandingdesk;

/**
 * Created by JunhyukLee on 15. 5. 26..
 */
public class MoveThread extends Thread {

    private BTService mBTService = null;
    private String mCommand = null;

    public MoveThread(BTService btService, String command) {
        mBTService = btService;
        mCommand = command;
    }

    @Override
    public void run() {
        long elapsedTime = System.nanoTime();

        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(1);
                if (System.nanoTime() - elapsedTime > 100000000) {
                    // pass 100 ms
                    elapsedTime = System.nanoTime();

                    if (mBTService == null)
                        continue;

                    if (mBTService.getState()
                            != BTService.STATE_CONNECTED)
                        continue;

                    if(mCommand == null)
                        continue;

                    if (mCommand.length() > 0) {
                        byte[] send = mCommand.getBytes();
                        mBTService.write(send);
                    }
                }
            }
        } catch (InterruptedException interruptedException) {

        }
    }

    public void cancel() {
        interrupt();
    }


}

