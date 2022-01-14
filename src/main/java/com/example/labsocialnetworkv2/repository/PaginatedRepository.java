package com.example.labsocialnetworkv2.repository;

import com.example.labsocialnetworkv2.domain.Entity;

public interface PaginatedRepository<ID, EType extends Entity<ID>> extends Repository<ID, EType> {
    Iterable<EType> findAllPage(int pageNumber, int rowsOnPage, Integer userId);
    int findTotalSize(Integer userId);

}
