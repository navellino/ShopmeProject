package it.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import it.shopme.common.entity.Role;
import it.shopme.common.entity.User;


@Service
@Transactional
public class UserService {
	
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private PasswordEncoder passEnc;
	
	
	public List<User> listAll(){
		return (List<User>) repo.findAll();
	}
	
	public List<Role> roleList(){
		return (List<Role>) roleRepo.findAll(); 
	}
	
	public void saveUser(User user) {
		boolean isUptadingUser = (user.getId() != null);
		
		if(isUptadingUser) {
			User existingUser = repo.findById(user.getId()).get();
			if(user.getPassword().isEmpty()) {
				user.setPassword(existingUser.getPassword());
			}else {
				encodePassword(user);
			}	
		}else {
			encodePassword(user);
		}
		//encodePassword(user);
		repo.save(user);
	}
	
	private void encodePassword(User user) {
		String encodedPassword = passEnc.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}
	
	public boolean isEmailUnique(Integer id, String email) {
		User userByEmail = repo.findByEmail(email);


		if (userByEmail == null) return true;
		
		boolean isCreatingNew = (id == null);
		
		if (isCreatingNew) {
			if (userByEmail != null) return false;
		} else {
			if (userByEmail.getId() != id) {
				return false;
			}
		}
		return true;
	}
	
	public void updateUserEnabledStatus(Integer id, boolean enabled) {
		repo.updateEnableStatus(id, enabled);
	}
	
	public User getUserById(Integer id) throws UserNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new UserNotFoundException("Nessun utente trovato con id: "+id);
		}
	}
	
	public void delete(Integer id) throws UserNotFoundException {
		Long countById = repo.countById(id);
		if (countById == null || countById == 0) {
			throw new UserNotFoundException("Nessun utente trovato con id: "+id);
		}
		repo.deleteById(id);
	}
}
