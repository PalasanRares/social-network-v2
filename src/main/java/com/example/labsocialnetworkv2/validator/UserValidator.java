package com.example.labsocialnetworkv2.validator;

import com.example.labsocialnetworkv2.domain.User;
import com.example.labsocialnetworkv2.validator.exception.EmptyFieldException;
import com.example.labsocialnetworkv2.validator.exception.ImpossibleAgeException;
import com.example.labsocialnetworkv2.validator.exception.InvalidPasswordException;
import com.example.labsocialnetworkv2.validator.exception.ValidationException;

import java.time.LocalDate;

/**
 * Validates all the user's attributes
 */
public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        if (entity.getUsername().equals("")) {
            throw new EmptyFieldException("Username cannot be empty");
        }
        if (entity.getPassword().equals("")) {
            throw new EmptyFieldException("Password cannot be empty");
        }
        if (entity.getPassword().length()<6) {
            throw new InvalidPasswordException("Password has to contain over 6 characters");
        }
        if (entity.getFirstName().equals("")) {
            throw new EmptyFieldException("First name cannot be empty");
        }
        if (entity.getLastName().equals("")) {
            throw new EmptyFieldException("Last name cannot be empty");
        }
        if (entity.getBirthday().isBefore(LocalDate.parse("1900-01-01"))) {
            throw new ImpossibleAgeException("That person cannot be alive anymore");
        }
    }
}
