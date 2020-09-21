/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.domain;

/**
 * Goods
 *
 * @author QuanyeChen
 */
public class Goods {
    private String name;
    private Float price;

    public Goods() {
    }

    public Goods(String name, Float price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Goods{" + "name=" + name + ", size=" + price + '}';
    }


}
