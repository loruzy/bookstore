package com.online.book.store.repositories;

import com.online.book.store.entities.OrderRegistration;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<OrderRegistration, Integer> {
    List<OrderRegistration> findAll();

    List<OrderRegistration> findByBusername(String busername);

}
