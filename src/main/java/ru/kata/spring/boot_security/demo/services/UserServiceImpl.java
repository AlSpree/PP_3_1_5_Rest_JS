package ru.kata.spring.boot_security.demo.services;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.DTO.RoleDTO;
import ru.kata.spring.boot_security.demo.DTO.UserDTO;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.*;


@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }


    public List<User> findAll () {
        return userRepository.findAll();
    }


    public User getUserById(int id) {
        Optional<User> user = userRepository.findUserById(id);
        return user.orElse(null);
    }

    @Transactional
    public void saveNewUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(User user, int id) {
    user.setId(id);
    userRepository.save(user);
    }

    @Transactional
    public void delete(int id) {
        userRepository.deleteById(id);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userFound = userRepository.findUserByName(username);
        if(userFound.isEmpty()){
            throw new UsernameNotFoundException(String.format("User '%s' not found!", username));
        }
        return new org.springframework.security.core.userdetails.User(userFound.get().getName(), userFound.get().getPassword(), userFound.get().getAuthorities());
    }

    public User getUser(String username) {
        return userRepository.findUserByName(username).get();
    }

    public Set<Role> getAllRoles() {
        return new HashSet<>(roleRepository.findAll());
    }


//Convertors
    public User userDTOConvertToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
    public UserDTO userConvertToUserDTO (User user) {
        return modelMapper.map(user, UserDTO.class);
    }
    public RoleDTO roleConvertToRoleDTO(Role role) {
        return modelMapper.map(role, RoleDTO.class);
    }

}
