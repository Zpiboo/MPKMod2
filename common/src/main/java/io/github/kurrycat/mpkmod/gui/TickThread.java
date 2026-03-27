package io.github.kurrycat.mpkmod.gui;

import io.github.kurrycat.mpkmod.compatibility.API;

import java.util.ArrayList;
import java.util.List;

public class TickThread implements Runnable {
    private static final Object lock = new Object();

    private static final List<Tickable> tickables = new ArrayList<>();
    private static boolean changed = false;

    public static void startThread() {
        Thread t = new Thread(
                new TickThread(),
                API.MODID + " GUI Tick Thread"
        );
        t.setDaemon(true);
        t.start();
    }

    public static void setTickables(List<Tickable> tickables) {
        synchronized (lock) {
            TickThread.tickables.clear();
            TickThread.tickables.addAll(tickables);
            changed = true;

            lock.notify();
        }
    }

    @Override
    public void run() {
        API.LOGGER.info("Started GuiTickThread");
        try {
            while (!Thread.currentThread().isInterrupted()) {
                List<Tickable> tickablesSnapshot;

                synchronized (lock) {
                    while (!changed) {
                        lock.wait();
                    }
                    changed = false;
                    tickablesSnapshot = new ArrayList<>(tickables);
                }

                for (Tickable tickable : tickablesSnapshot) {
                    try {
                        tickable.tick();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
    }

    public interface Tickable {
        void tick();
    }
}
