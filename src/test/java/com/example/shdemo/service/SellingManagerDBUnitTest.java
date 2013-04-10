package com.example.shdemo.service;

import com.example.shdemo.domain.Address;
import com.example.shdemo.domain.Car;
import com.example.shdemo.domain.Person;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.github.springtestdbunit.assertion.DatabaseAssertionMode.NON_STRICT;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/beans.xml"})
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = true)
@Transactional
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class
})
public class SellingManagerDBUnitTest {
    private static final String NAME_1 = "Jakub";
    private static final String PIN_1 = "3578";

    private static final String NAME_2 = "Mariusz";
    private static final String PIN_2 = "1598";

    private static final String NAME_3 = "Paulina";
    private static final String PIN_3 = "8235";

    private static final String NAME_4 = "Natalia";
    private static final String PIN_4 = "7648";

    private static final String MODEL_1 = "5008";
    private static final String MAKE_1 = "Peugeot";

    private static final String MODEL_2 = "Sedici";
    private static final String MAKE_2 = "Fiat";

    private static final String MODEL_3 = "A6";
    private static final String MAKE_3 = "Audi";

    @Autowired
    SellingManager sellingManager;

    @Test
    @DatabaseSetup("/fullData.xml")
    @ExpectedDatabase(value = "/addPersonData.xml", assertionMode = NON_STRICT)
    public void addClientCheck() {
        sellingManager.addClient(createPerson(NAME_1, PIN_1));
    }

    @Test
    @DatabaseSetup("/fullData.xml")
    @ExpectedDatabase(value = "/addCarData.xml", assertionMode = NON_STRICT)
    public void addCarCheck() {
        sellingManager.addNewCar(createCar(MAKE_1, MODEL_1));
    }

    @Test
    @DatabaseSetup("/fullData.xml")
    @ExpectedDatabase(value = "/sellCarData.xml", assertionMode = NON_STRICT)
    public void sellCarCheck() {
        Person person = createPerson(NAME_2, PIN_2);
        Car car = createCar(MAKE_2, MODEL_2);

        Long carId = sellingManager.addNewCar(car);
        sellingManager.addClient(person);

        Person retrievedPerson = sellingManager.findClientByPin(PIN_2);
        sellingManager.sellCar(retrievedPerson.getId(), carId);

        List<Car> ownedCars = sellingManager.getOwnedCars(retrievedPerson);

        assertEquals(1, ownedCars.size());

        Car car1 = ownedCars.get(0);
        assertEquals(MAKE_2, car1.getMake());
        assertEquals(MODEL_2, car1.getModel());
        assertTrue(car1.getSold());
    }

    @Test
    @DatabaseSetup("/fullData.xml")
    @ExpectedDatabase(value = "/disposeCarData.xml", assertionMode = NON_STRICT)
    public void disposeCarCheck() {
        final List<Long> carsId = new ArrayList<>(3);

        sellingManager.addClient(createPerson(NAME_3, PIN_3));

        carsId.add(sellingManager.addNewCar(createCar(MAKE_1, MODEL_1)));
        carsId.add(sellingManager.addNewCar(createCar(MAKE_2, MODEL_2)));
        carsId.add(sellingManager.addNewCar(createCar(MAKE_3, MODEL_3)));

        Person person = sellingManager.findClientByPin(PIN_3);
        assertTrue(person.getCars().isEmpty());

        for (Long idx : carsId) {
            sellingManager.sellCar(person.getId(), idx);
        }

        person = sellingManager.findClientByPin(PIN_3);
        List<Car> ownedCars = sellingManager.getOwnedCars(person);

        for (Car c : ownedCars) {
            assertThat(c.getId(), isIn(carsId));
        }

        Car car = (Car) CollectionUtils.find(ownedCars, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return object instanceof Car && ((Car) object).getId().equals(carsId.get(0));
            }
        });

        carsId.remove(car.getId());
        sellingManager.disposeCar(person, car);

        person = sellingManager.findClientByPin(PIN_3);
        ownedCars = sellingManager.getOwnedCars(person);

        for (Car c : ownedCars) {
            assertThat(c.getId(), isIn(carsId));
        }
    }

    @Test
    @DatabaseSetup("/fullData.xml")
    @ExpectedDatabase(value = "/addAddressesData.xml", assertionMode = NON_STRICT)
    public void personAddressEagerFetchingTest() {
        Person person1 = createPerson(NAME_4, PIN_4);

        Address address1 = new Address();
        address1.setCity("Gdynia");
        address1.setPostcode("13-678");
        address1.setStreet("Grunwaldzka");
        address1.setPerson(person1);

        Address address2 = new Address();
        address2.setCity("Gdańsk");
        address2.setPostcode("13-987");
        address2.setStreet("Dluga");
        address2.setPerson(person1);

        sellingManager.addAddress(address1);
        sellingManager.addAddress(address2);
        Person person = sellingManager.findClientByPin(PIN_4);

        assertThat(sellingManager.getPersonAddresses(person), hasItems(address1, address2));
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
