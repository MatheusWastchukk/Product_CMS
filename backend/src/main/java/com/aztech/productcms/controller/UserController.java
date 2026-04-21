package com.aztech.productcms.controller;

import com.aztech.productcms.dto.PageResponseDTO;
import com.aztech.productcms.dto.UserCreateRequestDTO;
import com.aztech.productcms.dto.UserResponseDTO;
import com.aztech.productcms.dto.UserUpdateRequestDTO;
import com.aztech.productcms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public PageResponseDTO<UserResponseDTO> list(@PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return PageResponseDTO.from(userService.list(pageable));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO create(@Valid @RequestBody UserCreateRequestDTO request) {
        return userService.create(request);
    }

    @PutMapping("/{id}")
    public UserResponseDTO update(@PathVariable("id") Long id, @Valid @RequestBody UserUpdateRequestDTO request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        userService.delete(id);
    }
}
