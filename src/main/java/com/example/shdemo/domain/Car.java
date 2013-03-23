package com.example.shdemo.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.Objects;

@Entity
@NamedQueries({
        @NamedQuery(name = "car.unsold", query = "Select c from Car c where c.sold = false")
})
public class Car {
    private Long id;
    private String make;
    private String model;
    private Boolean sold = false;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Boolean getSold() {
        return sold;
    }

    public void setSold(Boolean sold) {
        this.sold = sold;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, make, model, sold);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Car other = (Car) obj;

        return Objects.equals(this.id, other.id) &&
                Objects.equals(this.make, other.make) &&
                Objects.equals(this.model, other.model) &&
                Objects.equals(this.sold, other.sold);
    }
}
