package com.bytestree.restful.integration.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import com.bytestree.restful.model.Employee;

/**
 * Integration test for EmployeeController Initial data gets loaded from
 * init-data.sql in test classpath
 * 
 * @author bytesTree
 * @see <a href="http://www.bytestree.com/">BytesTree</a>
 * 
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@Sql({ "classpath:init-data.sql" })
public class EmployeeControllerTest {

	@Autowired
	private TestRestTemplate restTemplate;

	private static final String URL = "/employee/";

	@Test
	public void testAddEmployee() throws Exception {

		// prepare
		Employee employee = new Employee("bytes", "tree", "developer", 12000);

		// execute
		ResponseEntity<Employee> responseEntity = restTemplate.postForEntity(URL, employee, Employee.class);

		// collect Response
		int status = responseEntity.getStatusCodeValue();
		Employee resultEmployee = responseEntity.getBody();

		// verify
		assertEquals("Incorrect Response Status", HttpStatus.CREATED.value(), status);

		assertNotNull(resultEmployee);
		assertNotNull(resultEmployee.getId().longValue());

	}

	@Test
	public void testGetEmployee() throws Exception {

		// prepare
		// Not required as init-data.sql will insert one record which will
		// be retrieved here

		// execute
		ResponseEntity<Employee> responseEntity = restTemplate.getForEntity(URL + "{id}", Employee.class, new Long(1));

		// collect response
		int status = responseEntity.getStatusCodeValue();
		Employee resultEmployee = responseEntity.getBody();

		// verify
		assertEquals("Incorrect Response Status", HttpStatus.OK.value(), status);

		assertNotNull(resultEmployee);
		assertEquals(1l, resultEmployee.getId().longValue());

	}

	@Test
	public void testGetEmployeeNotExist() throws Exception {

		// prepare data and mock's behaviour
		// Not Required as employee Not Exist scenario

		// execute
		ResponseEntity<Employee> responseEntity = restTemplate.getForEntity(URL + "{id}", Employee.class,
				new Long(100));

		// collect response
		int status = responseEntity.getStatusCodeValue();
		Employee resultEmployee = responseEntity.getBody();

		// verify
		assertEquals("Incorrect Response Status", HttpStatus.NOT_FOUND.value(), status);
		assertNull(resultEmployee);
	}

	@Test
	public void testGetAllEmployee() throws Exception {

		// prepare
		// Not required as data.sql will insert one record which will be
		// fetched by this Restful web service call

		// execute
		ResponseEntity<List> responseEntity = restTemplate.getForEntity(URL, List.class);

		// collect response
		int status = responseEntity.getStatusCodeValue();
		List<Employee> empListResult = null;
		if (responseEntity.getBody() != null) {
			empListResult = responseEntity.getBody();
		}

		// verify
		assertEquals("Incorrect Response Status", HttpStatus.OK.value(), status);

		assertNotNull("Employees not found", empListResult);
		assertEquals("Incorrect Employee List", 1, empListResult.size());

	}

	@Test
	public void testDeleteEmployee() throws Exception {

		// execute - delete the record added while initializing database with
		// test data

		ResponseEntity<Void> responseEntity = restTemplate.exchange(URL + "{id}", HttpMethod.DELETE, null, Void.class,
				new Long(1));

		// verify
		int status = responseEntity.getStatusCodeValue();
		assertEquals("Incorrect Response Status", HttpStatus.GONE.value(), status);

	}

	@Test
	public void testUpdateEmployee() throws Exception {
		// prepare
		// here the create the employee object with ID equal to ID of
		// employee need to be updated with updated properties
		Employee employee = new Employee(1l, "bytes", "tree", "developer", 15000);
		HttpEntity<Employee> requestEntity = new HttpEntity<Employee>(employee);

		// execute
		ResponseEntity<Void> responseEntity = restTemplate.exchange(URL, HttpMethod.PUT, requestEntity, Void.class);

		// verify
		int status = responseEntity.getStatusCodeValue();
		assertEquals("Incorrect Response Status", HttpStatus.OK.value(), status);
	}

}
