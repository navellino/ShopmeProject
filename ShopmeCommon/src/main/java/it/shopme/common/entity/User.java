package it.shopme.common.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {
	private Integer id;
	private String firstName;
	private String lastName;
}
