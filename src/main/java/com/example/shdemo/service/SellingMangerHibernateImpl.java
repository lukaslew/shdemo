package com.example.shdemo.service;

import com.example.shdemo.domain.Address;
import com.example.shdemo.domain.Car;
import com.example.shdemo.domain.Person;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@Transactional
public class SellingMangerHibernateImpl implements SellingManager {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void addClient(Person person) {
        person.setId(null);
        sessionFactory.getCurrentSession().persist(person);
    }

    @Override
    public void deleteClient(Person person) {
        Session currentSession = sessionFactory.getCurrentSession();
        person = (Person) currentSession.get(Person.class, person.getId());

        // lazy loading here
        for (Car car : person.getCars()) {
            car.setSold(false);
            currentSession.update(car);
        }

        currentSession.delete(person);
    }

    @Override
    public List<Car> getOwnedCars(Person person) {
        person = (Person) sessionFactory.getCurrentSession().get(Person.class, person.getId());

        return new ArrayList<>(person.getCars()); // lazy loading here - try this code without (shallow) copying
    }

    @Override
    public List<Person> getAllClients() {
        return cast(Person.class, sessionFactory.getCurrentSession().getNamedQuery("person.all").list());
    }

    @Override
    public Person findClientByPin(String pin) {
        return (Person) sessionFactory.getCurrentSession().getNamedQuery("person.byPin").setString("pin", pin).uniqueResult();
    }

    @Override
    public Long addNewCar(Car car) {
        car.setId(null);

        return (Long) sessionFactory.getCurrentSession().save(car);
    }

    @Override
    public void sellCar(Long personId, Long carId) {
        Session currentSession = sessionFactory.getCurrentSession();
        Person person = (Person) currentSession.get(Person.class, personId);
        Car car = (Car) currentSession.get(Car.class, carId);

        car.setSold(true);
        person.getCars().add(car);

        currentSession.update(car);
        currentSession.flush();
    }

    @Override
    public List<Car> getAvailableCars() {
        return cast(Car.class, sessionFactory.getCurrentSession().getNamedQuery("car.unsold").list());
    }

    @Override
    public void disposeCar(Person person, Car car) {
        Session currentSession = sessionFactory.getCurrentSession();
        person = (Person) currentSession.get(Person.class, person.getId());
        car = (Car) currentSession.get(Car.class, car.getId());

        Car toRemove = null;
        // lazy loading here (person.getCars)
        for (Car aCar : person.getCars())
            if (aCar.getId().compareTo(car.getId()) == 0) {
                toRemove = aCar;
                break;
            }

        if (toRemove != null) {
            person.getCars().remove(toRemove);
        }

        car.setSold(false);
        currentSession.update(car);
        currentSession.flush();
    }

    @Override
    public Car findCarById(Long id) {
        return (Car) sessionFactory.getCurrentSession().get(Car.class, id);
    }

    @Override
    public void addAddress(Address address) {
        address.setId(null);
        sessionFactory.getCurrentSession().persist(address);
    }

    @Override
    public List<Address> getPersonAddresses(Person person) {
        return cast(Address.class, sessionFactory.getCurrentSession().getNamedQuery("address.byPerson").setEntity("person", person).list());
    }

    private <T> List<T> cast(Class<T> clazz, Collection collection) {
        List<T> list = new ArrayList<>(collection.size());

        for (Object obj : collection) {
            if (clazz.isInstance(obj)) {
                list.add(clazz.cast(obj));
            }
        }

        return list;
    }

}
