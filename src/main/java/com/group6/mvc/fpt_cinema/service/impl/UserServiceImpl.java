package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.entity.Users;
import com.group6.mvc.fpt_cinema.repository.UserRepository;
import com.group6.mvc.fpt_cinema.service.UserService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl
        extends AbstractCrudService<Users, Integer>
        implements UserService {
    @Autowired
    UserRepository userRepository;

    public UserServiceImpl(UserRepository repository) {
        super(repository);
    }


}
