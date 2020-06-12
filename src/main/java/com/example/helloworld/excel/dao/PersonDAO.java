package com.example.helloworld.excel.dao;

import com.example.helloworld.excel.entity.Person;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

public class PersonDAO extends AbstractDAO<Person> {

    public PersonDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public int create(Person person) {
        return persist(person).getId();
    }

}
