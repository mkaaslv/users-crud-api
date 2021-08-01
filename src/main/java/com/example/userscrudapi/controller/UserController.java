package com.example.userscrudapi.controller;

import com.example.userscrudapi.utils.UserSpecificationsBuilder;
import com.example.userscrudapi.model.User;
import com.example.userscrudapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        try {
            User newUser = userService.addUser(user);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);

        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users")
    public List<User> search(@RequestParam(value = "search") String search) {
        UserSpecificationsBuilder builder = new UserSpecificationsBuilder();
        Pattern pattern = Pattern.compile("(\\w+?)([:<>])(\\w+?),");
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
        }

        Specification<User> spec = builder.build();
        return userService.findAllUsers(spec);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> findAllUsers(@RequestParam(value= "email", required = false) String email,
                                                   @RequestParam(value= "username", required = false) String username) {
        try {
            List<User> users = new ArrayList<>();


            if (email == null && username == null) {
                users = userService.findAllActiveUsers();
            }
            // filter out users if email or username given
            else if (email != null) {
                users.add(userService.findUserByEmail(email));
            }
            else {
                users.add(userService.findUserByUsername(username));
            }

            // if no users
            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(users, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> findUserById(@PathVariable("id") Long id) {
        Optional<User> userData = userService.findUserById(id);

        return userData.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable Long id) {
        try {
            user.setId(id);
            User newUser = userService.updateUser(user);
            return new ResponseEntity<>(newUser, HttpStatus.OK);

        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/users")
    public ResponseEntity<?> deleteAllUsers() {
        try {
            userService.deleteAllUsers();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ADMIN
    @GetMapping("/admin/users")
    public ResponseEntity<List<User>> findOverallUsers() {
        List<User> users = userService.findAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/admin/users/deleted")
    public ResponseEntity<List<User>> findDeletedUsers() {
        List<User> users = userService.findAllDeletedUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

}
