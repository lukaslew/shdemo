package com.example.shdemo.service;

import com.example.shdemo.domain.Car;
import com.example.shdemo.domain.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/beans.xml"})
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = true)
@Transactional
public class SellingManagerTest {

    @Autowired
    SellingManager sellingManager;

    private static final String NAME_1 = "Bolek";
    private static final String PIN_1 = "1234";

    private static final String NAME_2 = "Lolek";
    private static final String PIN_2 = "4321";

    private static final String NAME_3 = "Andrzej";
    private static final String PIN_3 = "5678";

    private static final String MODEL_1 = "126p";
    private static final String MAKE_1 = "Fiat";

    private static final String MODEL_2 = "Mondeo";
    private static final String MAKE_2 = "Ford";

    private static final String MODEL_3 = "Civic";
    private static final String MAKE_3 = "Honda";

    @Test
    public void addClientCheck() {
        List<Person> retrievedClients = sellingManager.getAllClients();

        // If there is a client with PIN_1 delete it
        for (Person client : retrievedClients) {
            if (client.getPin().equals(PIN_1)) {
                sellingManager.deleteClient(client);
            }
        }

        Person person = createPerson(NAME_1, PIN_1);
        // ... other properties here

        // Pin is Unique
        sellingManager.addClient(person);

        Person retrievedClient = sellingManager.findClientByPin(PIN_1);

        assertEquals(NAME_1, retrievedClient.getFirstName());
        assertEquals(PIN_1, retrievedClient.getPin());
        // ... check other properties here
    }

    @Test
    public void addCarCheck() {
        Car car = createCar(MAKE_1, MODEL_1);
        // ... other properties here

        Long carId = sellingManager.addNewCar(car);

        Car retrievedCar = sellingManager.findCarById(carId);
        assertEquals(MAKE_1, retrievedCar.getMake());
        assertEquals(MODEL_1, retrievedCar.getModel());
        // ... check other properties here
    }

    @Test
    public void sellCarCheck() {
        Person person = createPerson(NAME_2, PIN_2);

        sellingManager.addClient(person);

        Person retrievedPerson = sellingManager.findClientByPin(PIN_2);

        Car car = createCar(MAKE_2, MODEL_2);

        Long carId = sellingManager.addNewCar(car);

        sellingManager.sellCar(retrievedPerson.getId(), carId);

        List<Car> ownedCars = sellingManager.getOwnedCars(retrievedPerson);

        assertEquals(1, ownedCars.size());
        assertEquals(MAKE_2, ownedCars.get(0).getMake());
        assertEquals(MODEL_2, ownedCars.get(0).getModel());
    }

    @Test
    public void disposeCarCheck() {
        sellingManager.addClient(createPerson(NAME_3, PIN_3));

        sellingManager.addNewCar(createCar(MAKE_1, MODEL_1));
        sellingManager.addNewCar(createCar(MAKE_2, MODEL_2));
        sellingManager.addNewCar(createCar(MAKE_3, MODEL_3));

        Person person = sellingManager.findClientByPin(PIN_3);
        assertTrue(person.getCars().isEmpty());

        List<Car> availableCars = sellingManager.getAvailableCars();
        for (Car c : availableCars) {
            sellingManager.sellCar(person.getId(), c.getId());
        }

        person = sellingManager.findClientByPin(PIN_3);
        assertThat(person.getCars(), hasItems(availableCars.toArray(new Car[availableCars.size()])));

        assertThat(sellingManager.getAvailableCars(), hasSize(0));

        sellingManager.disposeCar(person, person.getCars().get(1));

        availableCars = sellingManager.getAvailableCars();
        person = sellingManager.findClientByPin(PIN_3);

        assertThat(availableCars, not(hasSize(0)));
        assertThat(person.getCars(), not(hasItems(availableCars.toArray(new Car[availableCars.size()]))));
    }

    private Person createPerson(String name, String pin) {
        Person p = new Person();
        p.setFirstName(name);
        p.setPin(pin);

        return p;
    }

    private Car createCar(String make, String model) {
        Car c = new Car();
        c.setMake(make);
        c.setModel(model);

        return c;
    }

}
