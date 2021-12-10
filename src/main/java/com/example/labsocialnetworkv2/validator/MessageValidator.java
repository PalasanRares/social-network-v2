package com.example.labsocialnetworkv2.validator;

import com.example.labsocialnetworkv2.domain.Message;
import com.example.labsocialnetworkv2.validator.exception.EmptyFieldException;
import com.example.labsocialnetworkv2.validator.exception.ValidationException;

public class MessageValidator implements Validator<Message>{
    @Override
    public void validate(Message entity) throws ValidationException {
        if (entity.getReceivers().size()==0) {
            throw new EmptyFieldException("The message must be sent to someone!");
        }
        if (entity.getMessage().equals("")) {
            throw new EmptyFieldException("Message cannot be empty");
        }
    }
}
