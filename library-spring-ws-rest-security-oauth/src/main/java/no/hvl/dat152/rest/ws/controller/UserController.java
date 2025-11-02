/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.service.UserService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/users")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Object> getUsers() {

		List<User> users = userService.findAllUsers();

		if (users.isEmpty())

			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		else
			return new ResponseEntity<>(users, HttpStatus.OK);
	}

	@GetMapping(value = "/users/{id}")
	@PreAuthorize("hasAuthority('ADMIN') or @userService.findUser(#id).getEmail() == authentication.name")
	public ResponseEntity<Object> getUser(@PathVariable Long id) throws UserNotFoundException, OrderNotFoundException {

		User user = userService.findUser(id);

		return new ResponseEntity<>(user, HttpStatus.OK);

	}

	@PostMapping("/users")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Object> createUser(@RequestBody User user) {
		User savedUser = userService.saveUser(user);
		return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
	}

	@PutMapping("/users/{id}")
	@PreAuthorize("hasAuthority('ADMIN') or @userService.findUser(#id).getEmail() == authentication.name")
	public ResponseEntity<Object> updateUser(@RequestBody User user, @PathVariable Long id) throws UserNotFoundException {
		User updatedUser = userService.updateUser(user, id);
		return new ResponseEntity<>(updatedUser, HttpStatus.OK);
	}

	@DeleteMapping("/users/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Object> deleteUser(@PathVariable Long id)
			throws UserNotFoundException {
		userService.deleteUser(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/users/{id}/orders")
	@PreAuthorize("hasAuthority('ADMIN') or @userService.findUser(#id).getEmail() == authentication.name")
	public ResponseEntity<Object> getUserOrders(@PathVariable Long id)
			throws UserNotFoundException {
		Set<Order> orders = userService.getUserOrders(id);
		if (orders.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}

	@GetMapping("/users/{uid}/orders/{oid}")
	@PreAuthorize("hasAuthority('ADMIN') or @userService.findUser(#uid).getEmail() == authentication.name")
	public ResponseEntity<Object> getUserOrder(@PathVariable Long uid, @PathVariable Long oid)
			throws UserNotFoundException, OrderNotFoundException {
		Order order = userService.getUserOrder(uid, oid);
		return new ResponseEntity<>(order, HttpStatus.OK);
	}

	@DeleteMapping("/users/{uid}/orders/{oid}")
	@PreAuthorize("hasAuthority('ADMIN') or @userService.findUser(#uid).getEmail() == authentication.name")
	public ResponseEntity<Object> deleteUserOrder(@PathVariable Long uid, @PathVariable Long oid)
			throws UserNotFoundException, OrderNotFoundException {
		userService.deleteOrderForUser(uid, oid);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/users/{uid}/orders")
	@PreAuthorize("hasAuthority('ADMIN') or @userService.findUser(#uid).getEmail() == authentication.name")
	public ResponseEntity<Object> createUserOrder(
			@PathVariable Long uid,
			@RequestBody Order order)
			throws UserNotFoundException, OrderNotFoundException {

		Order savedOrder = userService.createOrdersForUser(uid, order);

		Link selfLink = linkTo(methodOn(UserController.class).getUserOrder(uid, savedOrder.getId())).withSelfRel();
		Link allOrdersLink = linkTo(methodOn(UserController.class).getUserOrders(uid)).withRel("all-orders");
		Link userLink = linkTo(methodOn(UserController.class).getUser(uid)).withRel("user");
		Link deleteLink = linkTo(methodOn(UserController.class).deleteUserOrder(uid, savedOrder.getId()))
				.withRel("delete-order");

		savedOrder.add(selfLink);
		savedOrder.add(allOrdersLink);
		savedOrder.add(userLink);
		savedOrder.add(deleteLink);

		return new ResponseEntity<>(List.of(savedOrder), HttpStatus.CREATED);
	}

}