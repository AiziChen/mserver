package org.quanye.sobj.struct;

/**
 * Cons
 * This source code is license on the Apache-License v2.0
 *
 * @author QuanyeChen
 */
public class SObjNode {
    private String carValue;
    private SObjNode car;
    private SObjNode cdr;

    public SObjNode(String carValue) {
        this.carValue = carValue;
    }

    public String getCarValue() {
        return carValue;
    }

    public void setCarValue(String carValue) {
        this.carValue = carValue;
    }

    public SObjNode getCar() {
        return car;
    }

    public void setCar(SObjNode car) {
        this.car = car;
    }

    public SObjNode getCdr() {
        return cdr;
    }

    public void setCdr(SObjNode cdr) {
        this.cdr = cdr;
    }

    @Override
    public String toString() {
        return "Cons{" +
                "carValue='" + carValue + '\'' +
                ", car=" + car +
                ", cdr=" + cdr +
                '}';
    }
}