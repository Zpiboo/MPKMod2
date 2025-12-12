package io.github.kurrycat.mpkmod.message;

import java.util.ArrayList;
import java.util.List;

public class MessageHandler {
    private final List<MessageReceiver> receivers = new ArrayList<>();

    public void registerReceiver(MessageReceiver receiver) {
        receivers.add(receiver);
    }
}
