package it.shopme.admin.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import it.shopme.common.entity.Customer;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, Integer> {
	
	@Query("SELECT c FROM Customer c  WHERE CONCAT(c.email, ' ',c.firstName, ' ',c.lastName,' ',c.city,' ',c.state,' ',"
			+"c.postalCode,' ',c.country.name) LIKE %?1%")
	public Page<Customer> findAll(String keyword, Pageable pageable);
	
	@Query("UPDATE Customer c SET c.enabled = ?2 WHERE c.id = ?1")
	@Modifying
	public void enableStatus(Integer id, boolean enable);
	
	@Query("SELECT c FROM Customer c WHERE c.email = ?1")
	public Customer findByEmail(String email);
	
	public Long countById(Integer id);
}
