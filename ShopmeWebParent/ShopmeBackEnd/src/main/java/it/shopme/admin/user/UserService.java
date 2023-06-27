package it.shopme.admin.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.shopme.common.entity.Role;
import it.shopme.common.entity.User;

@Service
public class UserService {
	
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	
	public List<User> listAll(){
		return (List<User>) repo.findAll();
	}
	
	public List<Role> roleList(){
		return (List<Role>) roleRepo.findAll(); 
	}
	
	public void saveUser(User user) {
		repo.save(user);
	}
}
