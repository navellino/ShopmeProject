package it.shopme.admin.setting;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.shopme.admin.setting.country.CountryRepository;
import it.shopme.common.entity.Country;

@SpringBootTest
@AutoConfigureMockMvc
public class CountryRestControllerTest {
	
	@Autowired MockMvc mockMvc;
	
	@Autowired ObjectMapper mapper;
	
	@Autowired CountryRepository repo;
	
	@Test
	@WithMockUser(username = "navellino@gmail.com", password = "nicola1234", roles = "ADMIN")
	public void testListCountries() throws Exception {
		String url = "/countries/list";
		MvcResult result = mockMvc.perform(get(url))
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn();
		
		String jsonResponse = result.getResponse().getContentAsString();
		
		Country[] countries = mapper.readValue(jsonResponse, Country[].class);
		/*for(Country country : countries) {
			System.out.println(country);
		}*/
		
		assertThat(countries).hasSizeGreaterThan(0);
	}
	
	@Test
	@WithMockUser(username = "navellino@gmail.com", password = "nicola1234", roles = "ADMIN")
	public void saveCountry() throws JsonProcessingException, Exception {
		String url = "/countries/save";
		String countryName = "France";
		String countryCode = "FR";
		Country country = new Country(countryName,countryCode);
		
		MvcResult result = mockMvc.perform(post(url).contentType("application/json")
				.content(mapper.writeValueAsString(country)).with(csrf())).andDo(print())
				.andExpect(status().isOk()).andReturn();
		
		String response = result.getResponse().getContentAsString();
		Integer countryId = Integer.parseInt(response);
		Country savedCountry = repo.findById(countryId).get();
		
		assertThat(savedCountry.getName()).isEqualTo(countryName);
	}
	
	@Test
	@WithMockUser(username = "navellino@gmail.com", password = "nicola1234", roles = "ADMIN")
	public void testUpdateCountry() throws JsonProcessingException, Exception {
		String url = "/countries/save";
		String countryName = "Francia";
		String countryCode = "FR";
		Integer countryId = 7;
		
		Country country = new Country(countryId,countryName,countryCode);
		
		mockMvc.perform(post(url).contentType("application/json")
				.content(mapper.writeValueAsString(country)).with(csrf())).andDo(print())
				.andExpect(status().isOk())
				.andExpect((ResultMatcher) content().string(String.valueOf(countryId)));

		//Integer countryId = Integer.parseInt(response);
		Country savedCountry = repo.findById(countryId).get();
		
		assertThat(savedCountry.getName()).isEqualTo(countryName);
	}
	
	@Test
	@WithMockUser(username = "navellino@gmail.com", password = "nicola1234", roles = "ADMIN")
	public void testDeletCountry() throws Exception {
		Integer countryId = 7;
		String url = "/countries/delete/"+countryId;
		
		mockMvc.perform(get(url)).andExpect(status().isOk());
		
		Optional<Country> savedCountry = repo.findById(countryId);
		
		assertThat(savedCountry).isNotPresent();
	}
}
