package it.shopme.admin.setting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.shopme.admin.setting.country.CountryRepository;
import it.shopme.admin.setting.state.StateRepository;
import it.shopme.common.entity.Country;
import it.shopme.common.entity.State;

@SpringBootTest
@AutoConfigureMockMvc
public class StateRestControllerTest {

	@Autowired MockMvc mokMvc;
	@Autowired ObjectMapper mapper;
	@Autowired CountryRepository countryRepo;
	@Autowired StateRepository stateRepo;
	
	@Test
	@WithMockUser(username = "navellino@gmail.com", password = "nicola1234", roles = "ADMIN")
	public void testByCountry() throws Exception {
		Integer countryId = 3;
		String url = "/states/list_by_country/"+countryId;
		
		MvcResult result = mokMvc.perform(get(url))
							.andExpect(status().isOk())
							.andDo(print())
							.andReturn();
		
		String jsonResponse = result.getResponse().getContentAsString();
		State[] states = mapper.readValue(jsonResponse, State[].class);
		
		assertThat(states).hasSizeGreaterThan(1);	
	}
	
	@Test
	@WithMockUser(username = "navellino@gmail.com", password = "nicola1234", roles = "ADMIN")
	public void testCreateState() throws JsonProcessingException, Exception {
		String url = "/states/save";
		Integer countryId = 3;
		Country country = countryRepo.findById(countryId).get();
		State state = new State("Piemonte", country);
		
		MvcResult result = mokMvc.perform(post(url).contentType("application/json")
							.content(mapper.writeValueAsString(state))
							.with(csrf()))
							.andDo(print())
							.andExpect(status().isOk()).andReturn();
		
		String response = result.getResponse().getContentAsString();
		Integer sateId  = Integer.parseInt(response);
		State findSateById = stateRepo.findById(sateId).get();
		
		assertThat(findSateById).isNotNull();
	}	
	
	@Test
	@WithMockUser(username = "navellino@gmail.com", password = "nicola1234", roles = "ADMIN")
	public void testUpdateState() throws JsonProcessingException, Exception {
		String url = "/states/save";
		Integer stateId = 16;
		String stateName = "Marche";
		
		State state = stateRepo.findById(stateId).get();
		state.setName(stateName);
		
		mokMvc.perform(post(url).contentType("application/json")
				.content(mapper.writeValueAsString(state))
				.with(csrf()))
				.andDo(print())
				.andExpect(status().isOk());
		
		State state2 = stateRepo.findById(stateId).get();
		assertThat(state2).isNotNull();
		
		assertThat(state2.getName()).isEqualTo(stateName);
	}
	
	@Test
	@WithMockUser(username = "navellino@gmail.com", password = "nicola1234", roles = "ADMIN")
	public void testDeleteState() throws JsonProcessingException, Exception {
		Integer stateId = 18;
		String url = "/states/delete/"+stateId;
		mokMvc.perform(get(url)).andExpect(status().isOk());
		
		State state2 = stateRepo.findById(stateId).get();
		assertThat(state2.getName()).isNull();
	}
}
