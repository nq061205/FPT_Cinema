package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.entity.Users;
import com.group6.mvc.fpt_cinema.repository.UserRepository;
import com.group6.mvc.fpt_cinema.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl
        extends AbstractCrudService<Users, Integer>
        implements UserService {

    public UserServiceImpl(UserRepository repository) {
        super(repository);
    }
}
