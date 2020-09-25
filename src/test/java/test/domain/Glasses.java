/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.domain;

/**
 * Glasses
 *
 * @author Quanyec
 */
public class Glasses {
    private Integer id;
    private Double degree;
    private String color;

    public Glasses() {
    }

    public Glasses(Integer id, Double degree, String color) {
        this.id = id;
        this.degree = degree;
        this.color = color;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getDegree() {
        return degree;
    }

    public void setDegree(Double degree) {
        this.degree = degree;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Glasses{" + "id=" + id + ", degree=" + degree + ", color=" + color + '}';
    }
}
