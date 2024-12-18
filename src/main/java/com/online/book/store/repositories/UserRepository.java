package com.online.book.store.repositories;

import com.online.book.store.entities.UserRegistration;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<UserRegistration, String> {

    UserRegistration findByEmailAndPassword(String email, String password);

    List<UserRegistration> findAll();


}
