package org.quanye.sobj.struct;

/**
 * Cons
 * This source code is license on the Apache-License v2.0
 *
 * @author QuanyeChen
 */
public class Cons {
    private String carValue;
    private String cdrValue;
    private Cons car;
    private Cons cdr;

    public Cons(String carValue, String cdrValue) {
        this.carValue = carValue;
        this.cdrValue = cdrValue;
    }

    public String getCarValue() {
        return carValue;
    }

    public void setCarValue(String carValue) {
        this.carValue = carValue;
    }

    public String getCdrValue() {
        return cdrValue;
    }

    public void setCdrValue(String cdrValue) {
        this.cdrValue = cdrValue;
    }

    public Cons getCar() {
        return car;
    }

    public void setCar(Cons car) {
        this.car = car;
    }

    public Cons getCdr() {
        return cdr;
    }

    public void setCdr(Cons cdr) {
        this.cdr = cdr;
    }

    @Override
    public String toString() {
        return "Cons{" +
                "carValue='" + carValue + '\'' +
                ", cdrValue='" + cdrValue + '\'' +
                ", car=" + car +
                ", cdr=" + cdr +
                '}';
    }
}