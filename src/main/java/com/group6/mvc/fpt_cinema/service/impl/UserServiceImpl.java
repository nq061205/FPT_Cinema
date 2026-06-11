package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.dto.request.CreateAccountRequest;
import com.group6.mvc.fpt_cinema.dto.response.UserCreateAccountResponse;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.mapper.UserMapper;
import com.group6.mvc.fpt_cinema.repository.RoleRepository;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group6.mvc.fpt_cinema.entity.User;
import com.group6.mvc.fpt_cinema.repository.UserRepository;
import com.group6.mvc.fpt_cinema.service.UserService;

@Service
public class UserServiceImpl
        extends AbstractCrudService<User, Integer>
        implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;



    public UserServiceImpl(UserRepository repository) {
        super(repository);
    }


    @Override
    public UserCreateAccountResponse createAccount(CreateAccountRequest request) {
        if(userRepository.getUserByEmail(request.getEmail()) != null){
            throw new AppException(ErrorCode.EMAIL_EXIST);
        }
        if(userRepository.getUserByPhone((request.getPhone())) != null){
            throw new AppException(ErrorCode.PHONE_EXIST);
        }
        UserMapper mapper = new UserMapper();
        User user = mapper.toEntity(request);
        user.setRole(roleRepository.getRoleById(4));
        userRepository.save(user);
        return mapper.toCreateAccountResponse(mapper.toEntity(request));
    }
}
