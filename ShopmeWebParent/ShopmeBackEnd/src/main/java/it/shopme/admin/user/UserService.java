package it.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.shopme.admin.paging.PagingAndSortingHelper;
import it.shopme.common.entity.Role;
import it.shopme.common.entity.User;


@Service
@Transactional
public class UserService {
	
	public static final int USER_PER_PAGE = 4;
	
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private PasswordEncoder passEnc;
	
	
	public User getByEmail(String email) {
		return repo.findByEmail(email);
	}
	
	public List<User> listAll(){
		return (List<User>) repo.findAll(Sort.by("lastName").ascending());
	}
	
	//public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword)
	public void listByPage(int pageNum, PagingAndSortingHelper helper){
		Sort sort = Sort.by(helper.getSortField());
		sort = helper.getSortDir().equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, USER_PER_PAGE, sort);
		Page<User> page = null;
		
		if(helper.getKeyword() != null) {
			page = repo.findAll(helper.getKeyword(), pageable);
		}else{
			page = repo.findAll(pageable);
		}
		
		helper.updateModelAttribute(pageNum, page);
	}
	
	public List<Role> roleList(){
		return (List<Role>) roleRepo.findAll(); 
	}
	
	public User saveUser(User user) {
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
		return repo.save(user);
	}
	
	public User updateUserAccount(User userInForm) {
		User userInDB = repo.findById(userInForm.getId()).get();
		if(!userInForm.getPassword().isEmpty()) {
			userInDB.setPassword(userInForm.getPassword());
			encodePassword(userInDB);
		}
		
		if(userInForm.getPhotos() != null) {
			userInDB.setPhotos(userInForm.getPhotos());
		}
		
		userInDB.setFirstName(userInForm.getFirstName());
		userInDB.setLastName(userInForm.getLastName());
		
		return repo.save(userInDB);
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
