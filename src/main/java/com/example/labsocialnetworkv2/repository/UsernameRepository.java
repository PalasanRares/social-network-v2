package com.example.labsocialnetworkv2.repository;

import com.example.labsocialnetworkv2.domain.Entity;
import com.example.labsocialnetworkv2.domain.User;

public interface UsernameRepository <ID, EType extends Entity<ID>> extends Repository<ID, EType> {
     User getByUsername(String username);
     String getSalt(Integer id);

}
