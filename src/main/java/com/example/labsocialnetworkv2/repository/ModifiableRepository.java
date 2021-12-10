package com.example.labsocialnetworkv2.repository;


import com.example.labsocialnetworkv2.domain.Entity;

public interface ModifiableRepository<ID, EType extends Entity<ID>> extends Repository<ID, EType> {

    boolean modify(EType newEntity);

    Iterable<EType> findAllForId(ID id);
}
