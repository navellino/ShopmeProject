package it.shopme.admin.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.shopme.common.entity.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer>{

}
