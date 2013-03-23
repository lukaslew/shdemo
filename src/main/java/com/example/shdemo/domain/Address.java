package com.example.shdemo.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.Objects;

@Entity
@NamedQueries({
        @NamedQuery(name = "address.byPerson", query = "Select a from Address a where a.person = :person")
})
public class Address {
    private Long id;
    private String city;
    private String postcode;
    private String street;
    private Person person;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, city, postcode, street);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Address other = (Address) obj;

        return Objects.equals(this.id, other.id) &&
                Objects.equals(this.city, other.city) &&
                Objects.equals(this.postcode, other.postcode) &&
                Objects.equals(this.street, other.street);
    }

    @Override
    public String toString() {
        return String.format("Address{id=%d, city='%s', postcode='%s', street='%s'}", id, city, postcode, street);
    }
}
