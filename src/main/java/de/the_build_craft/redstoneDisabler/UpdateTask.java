package de.the_build_craft.redstoneDisabler;

import java.util.TimerTask;

public class UpdateTask extends TimerTask {
    int seconds;

    public UpdateTask(int seconds) {
        this.seconds = seconds;
    }

    @Override
    public void run() {
        if (seconds < 0) return;
        if (--seconds == 0) RedstoneDisabler.instance.stopServer();

        System.out.println(seconds);
    }
}
