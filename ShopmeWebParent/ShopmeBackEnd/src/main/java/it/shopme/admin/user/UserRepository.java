package it.shopme.admin.user;

import org.springframework.data.repository.CrudRepository;

import it.shopme.common.entity.User;

public interface UserRepository extends CrudRepository<User, Integer> {

}
