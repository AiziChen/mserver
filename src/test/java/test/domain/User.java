/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.domain;

import org.quanye.sobj.annotation.DateFormat;

import java.util.Date;

/**
 * User Domain
 *
 * @author Quanyec
 */
public class User {
    private Integer id;
    private String name;
    private Integer age;
    @DateFormat("yyyy-MM-dd hh:mm,ss")
    private Date birth;
    private Glasses glasses;
    private Double height;
    private Goods[] goods;
    private String[] behaviors;

    public User() {
    }

    public User(Integer id, String name, Integer age, Date birth, Glasses glasses, Double height, Goods[] goods, String[] behaviors) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.birth = birth;
        this.glasses = glasses;
        this.height = height;
        this.goods = goods;
        this.behaviors = behaviors;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public Glasses getGlasses() {
        return glasses;
    }

    public void setGlasses(Glasses glasses) {
        this.glasses = glasses;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Goods[] getGoods() {
        return goods;
    }

    public void setGoods(Goods[] goods) {
        this.goods = goods;
    }

    public String[] getBehaviors() {
        return behaviors;
    }

    public void setBehaviors(String[] behaviors) {
        this.behaviors = behaviors;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name=" + name + ", age=" + age + ", birth=" + birth + ", glasses=" + glasses + ", height=" + height + ", goods=" + goods + ", behaviors=" + behaviors + '}';
    }


}
