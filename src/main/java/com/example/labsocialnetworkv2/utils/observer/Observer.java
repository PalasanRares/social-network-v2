package com.example.labsocialnetworkv2.utils.observer;

import com.example.labsocialnetworkv2.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E event);
}
