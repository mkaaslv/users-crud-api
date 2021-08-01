package com.example.userscrudapi;

import com.example.userscrudapi.model.User;
import com.example.userscrudapi.repo.IUserRepository;
import com.example.userscrudapi.utils.SearchCriteria;
import com.example.userscrudapi.utils.UserSpecification;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;


import java.util.List;


@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration(classes = { PersistenceJPAConfig.class })
@Transactional
@TransactionConfiguration
public class JPASpecificationsTest {

    @Autowired
    private IUserRepository repository;

    private User userJohn;
    private User userTom;

    @Before
    public void init() {
        userJohn = new User();
        userJohn.setFirstName("John");
        userJohn.setLastName("Doe");
        userJohn.setEmail("john@doe.com");
        userJohn.setUsername("hiVerIvE");
        repository.save(userJohn);

        userTom = new User();
        userTom.setFirstName("Tom");
        userTom.setLastName("Doe");
        userTom.setEmail("tom@doe.com");
        userTom.setUsername("LEthiCKL");
        repository.save(userTom);
    }

    @Test
    public void givenLast_whenGettingListOfUsers_thenCorrect() {
        UserSpecification spec =
                new UserSpecification(new SearchCriteria("lastName", ":", "doe"));

        List<User> results = repository.findAll(spec);

        // assertThat(userJohn, isIn(results));
        // assertThat(userTom, isIn(results));
    }

    @Test
    public void givenFirstAndLastName_whenGettingListOfUsers_thenCorrect() {
        UserSpecification spec1 =
                new UserSpecification(new SearchCriteria("firstName", ":", "john"));
        UserSpecification spec2 =
                new UserSpecification(new SearchCriteria("lastName", ":", "doe"));

        List<User> results = repository.findAll(Specification.where(spec1).and(spec2));

        // assertThat(userJohn, isIn(results));
        // assertThat(userTom, not(isIn(results)));
    }

    @Test
    public void givenWrongFirstAndLast_whenGettingListOfUsers_thenCorrect() {
        UserSpecification spec1 =
                new UserSpecification(new SearchCriteria("firstName", ":", "Adam"));
        UserSpecification spec2 =
                new UserSpecification(new SearchCriteria("lastName", ":", "Fox"));

        List<User> results =
                repository.findAll(Specification.where(spec1).and(spec2));

        // assertThat(userJohn, not(isIn(results)));
        // assertThat(userTom, not(isIn(results)));
    }
}
