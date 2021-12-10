package com.example.labsocialnetworkv2.validator;

import com.example.labsocialnetworkv2.domain.Friendship;
import com.example.labsocialnetworkv2.validator.exception.IdZeroException;
import com.example.labsocialnetworkv2.validator.exception.ValidationException;

/**
 * Validates entity of type Friendship
 */
public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        if (entity.getId().getFirst() .getId()<= 0) {
            throw new IdZeroException("The first id cannot be less than one");
        }
        if (entity.getId().getSecond().getId() <= 0) {
            throw new IdZeroException("The second id cannot be less than one");
        }
    }
}
