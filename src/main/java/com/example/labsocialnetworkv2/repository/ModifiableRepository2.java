package com.example.labsocialnetworkv2.repository;

import com.example.labsocialnetworkv2.domain.Entity;
import com.example.labsocialnetworkv2.repository.Repository;

public interface ModifiableRepository2<ID, EType extends Entity<ID>> extends Repository<ID, EType> {

    boolean modify(EType newEntity,Boolean all);

    Iterable<EType> findAllForId(ID id);
}
