package com.example.labsocialnetworkv2.utils.events;

import com.example.labsocialnetworkv2.domain.User;

public class RemoveUserEvent implements Event {
    private User data, oldData;

    public RemoveUserEvent(User data) {
        this.data = data;
    }

    public RemoveUserEvent(User data, User oldData) {
        this.data = data;
        this.oldData = oldData;
    }
}
