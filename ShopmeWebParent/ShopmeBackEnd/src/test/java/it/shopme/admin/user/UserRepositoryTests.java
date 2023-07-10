package it.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import it.shopme.common.entity.Role;
import it.shopme.common.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
	
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private TestEntityManager manager;
	
	@Test
	public void testCreatUser() {
		Role roleAdmin = manager.find(Role.class, 1);
		User user = new User("navellino@gmail.com", "nico1234", "Nicola", "Avellino");
		user.addRole(roleAdmin);
		User savedUser =  repo.save(user);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreatUserTwoRoles() {
		Role roleEditor = manager.find(Role.class, 3);
		Role roleAssistant = manager.find(Role.class, 5);
		User user = new User("delucatomma@gmail.com", "tomma12345", "Tommaso", "Deluca");
		user.addRole(roleAssistant);
		user.addRole(roleEditor);
		User savedUser =  repo.save(user);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListAll() {
		Iterable<User> listAllUser = repo.findAll();
		listAllUser.forEach(user -> System.out.println(user));
	}
	
	@Test
	public void testUserById() {
		User user = repo.findById(1).get();
		System.out.println(user);
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testUpdateUser() {
		User user = repo.findById(1).get();
		user.setEnabled(true);
		user.setEmail("navellino@email.it");
		repo.save(user);
	}
	
	@Test
	public void testUpdateUserRoles() {
		User user = repo.findById(2).get();
		Role roleEditor = manager.find(Role.class, 3);
		Role roleOther = manager.find(Role.class, 4);
		user.getRoles().remove(roleEditor);
		user.addRole(roleOther);
		repo.save(user);
	}
	
	@Test
	public void testDeleteUser() {
		Integer deletedUser = 2;
		repo.deleteById(deletedUser);
	}
	
	@Test
	public void testFindByEmail() {
		User user = repo.findByEmail("delucatomma@gmail.com");
		System.out.println(user);
		assertThat(user).isNotNull();
	}
	
	@Test
	public void countById() {
		Integer id = 5;
		Long countById = repo.countById(id);
		System.out.println(countById);
		assertThat(countById).isNotNull().isGreaterThan(0);
	}
	
	@Test
	public void testDisableUser() {
		Integer id = 1;
		repo.updateEnableStatus(id, true);
	}
	
	@Test
	public void testListFirstPage() {
		int pageNumber = 0;
		int pageSize = 4;
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repo.findAll(pageable);
		
		List<User> listUsers = page.getContent();
		
		listUsers.forEach(user -> System.out.println(user));
		
		assertThat(listUsers.size()).isEqualTo(pageSize);
	}
	
	@Test
	public void testSearchUser() {
		String keyword = "Nic";
		int pageNumber = 0;
		int pageSize = 4;
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repo.findAll(keyword, pageable);
		List<User> listUsers = page.getContent();
		listUsers.forEach(user -> System.out.println(user));
		assertThat(listUsers.size()).isGreaterThan(0);		
	}
}
