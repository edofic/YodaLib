package com.edofic.yodalib.examples.dbbenchmark;

import com.edofic.yodalib.database.Column;
import com.edofic.yodalib.database.Table;

/**
 * User: andraz
 * Date: 4/5/12
 * Time: 11:38 PM
 */
@Table(name = "people", version = 1)
public class Person {
    @Column(name = "id", primaryKey = true, autoIncrement = true, position = 1)
    private long id;
    @Column(name = "name", position = 2)
    private long debt;
    @Column(name = "debt", position = 3)
    private String name;

    public Person() {
    }

    public Person(long id, String name, long debt) {
        this.debt = debt;
        this.id = id;
        this.name = name;
    }

    public Person(String name) {
        this.name = name;
    }

    public long getDebt() {
        return debt;
    }

    public void setDebt(long debt) {
        this.debt = debt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
