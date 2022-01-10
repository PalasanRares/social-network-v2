package com.example.labsocialnetworkv2.repository;


import com.example.labsocialnetworkv2.domain.Entity;
import com.example.labsocialnetworkv2.domain.Message;

import java.time.LocalDate;
import java.util.List;

public interface ConvRepository<ID, EType extends Entity<ID>> extends ModifiableRepository2<ID, EType> {
    List<Message> conversatie(Integer u1 , Integer u2);

    ID getMostRecentMessage();
    List<Message> getReceivedMessagesPeriod(Integer loggedinid, Integer id, LocalDate startDate, LocalDate endDate);
}
