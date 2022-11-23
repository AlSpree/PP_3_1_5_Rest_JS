package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.DTO.RoleDTO;
import ru.kata.spring.boot_security.demo.DTO.UserDTO;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.UserServiceImpl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@Transactional(readOnly = true)
@RequestMapping("/api/users")
public class RestUserController {

    private final UserServiceImpl userServiceImpl;


    public RestUserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping("")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> allUsers =userServiceImpl.findAll()
                .stream().map(userServiceImpl::userConvertToUserDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("id") int id) {
        if(userServiceImpl.getUserById(id)==null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            UserDTO userDTO = userServiceImpl.userConvertToUserDTO(userServiceImpl.getUserById(id));
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<User> showUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return new ResponseEntity<>(userServiceImpl.getUser(authentication.getName()), HttpStatus.OK);
    }

    @GetMapping("/roles")
    public ResponseEntity<Set<RoleDTO>> getAllRoles() {
        return new ResponseEntity<>(userServiceImpl.getAllRoles()
                .stream().map(userServiceImpl::roleConvertToRoleDTO)
                .collect(Collectors.toSet()), HttpStatus.OK);
    }

    @PostMapping("")
    @Transactional
    public ResponseEntity<UserDTO> addUser(@RequestBody UserDTO userDTO) {
        User newUser = userServiceImpl.userDTOConvertToUser(userDTO);
        userServiceImpl.saveNewUser(newUser);
        return new ResponseEntity<>(userServiceImpl.userConvertToUserDTO(newUser),HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @Transactional
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO, @PathVariable("id") int id) {
        User updatedUser = userServiceImpl.userDTOConvertToUser(userDTO);
        userServiceImpl.updateUser(updatedUser, id);
        return new ResponseEntity<>(userServiceImpl.userConvertToUserDTO(updatedUser), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") int id) {
        userServiceImpl.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
