package com.group6.mvc.fpt_cinema.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group6.mvc.fpt_cinema.dto.request.CreateAccountRequest;
import com.group6.mvc.fpt_cinema.dto.response.UserCreateAccountResponse;
import com.group6.mvc.fpt_cinema.entity.User;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.mapper.UserMapper;
import com.group6.mvc.fpt_cinema.repository.UserRepository;
import com.group6.mvc.fpt_cinema.service.UserService;

@Service
public class UserServiceImpl
        extends AbstractCrudService<User, Integer>
        implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public UserServiceImpl(UserRepository repository) {
        super(repository);
    }

    @Override
    public UserCreateAccountResponse createAccount(CreateAccountRequest request) {
        User user = userMapper.toEntity(request);
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new AppException(ErrorCode.USERNAME_NOT_BLANK);
        }

        userRepository.save(user);
        return userMapper.toCreateAccountResponse(user);
    }

}
