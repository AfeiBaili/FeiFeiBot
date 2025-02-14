package online.afeibaili.other;

import online.afeibaili.MessageHandler;

import java.util.Timer;
import java.util.TimerTask;

public class TimerImmersive extends TimerTask {
    public static final Timer TIMER = new Timer();

    @Override
    public void run() {
        MessageHandler.isImmersive = false;
    }
}
