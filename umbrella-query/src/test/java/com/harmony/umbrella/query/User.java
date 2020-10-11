package com.harmony.umbrella.query;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table
public class User {

    @Id
    String id;
    String name;

}
