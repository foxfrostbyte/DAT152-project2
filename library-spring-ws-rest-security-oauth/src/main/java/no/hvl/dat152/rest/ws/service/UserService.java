package no.hvl.dat152.rest.ws.service;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.repository.OrderRepository;
import no.hvl.dat152.rest.ws.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Service layer for managing users
 */
@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;

	public List<User> findAllUsers(){
        return (List<User>) userRepository.findAll();
	}
	
	public User findUser(Long userid) throws UserNotFoundException {
        return userRepository.findById(userid)
                .orElseThrow(()-> new UserNotFoundException("User with id: "+userid+" not found"));
	}

	public User saveUser(User user) {
        return userRepository.save(user);
    }

	public void deleteUser(Long id) throws UserNotFoundException {
		User user = findUser(id);
		userRepository.delete(user);
	}

	public User updateUser(User user, Long id) throws UserNotFoundException {
		User existing = findUser(id);

        if (user.getFirstname() != null)
            existing.setFirstname(user.getFirstname());
        if (user.getLastname() != null)
            existing.setLastname(user.getLastname());

		return userRepository.save(existing);
	}

	public Set<Order> getUserOrders(Long userid) throws UserNotFoundException {
		User user = findUser(userid);
		return user.getOrders();
	}

	public Order getUserOrder(Long userid, Long oid) throws UserNotFoundException, OrderNotFoundException {
		User user = findUser(userid);
		Set<Order> orders = user.getOrders();
		
		for (Order order : orders) {
			if (order.getId().equals(oid)) {
				return order;
			}
		}
		throw new OrderNotFoundException("Order with id: " + oid + " not found for user with id: " + userid);
	}

	public void deleteOrderForUser(Long userid, Long oid) throws UserNotFoundException, OrderNotFoundException {
		if (!orderRepository.existsById(oid)) {
			return;
		}
		
		Long orderUserId = orderRepository.findUserID(oid);
		if (orderUserId == null || !orderUserId.equals(userid)) {
			throw new OrderNotFoundException("Order with id: " + oid + " not found for user with id: " + userid);
		}
		
		User user = findUser(userid);
		user.getOrders().size();
		user.getOrders().removeIf(o -> o.getId() != null && o.getId().equals(oid));
		userRepository.saveAndFlush(user);
		orderRepository.deleteById(oid);
	}

	public Order createOrdersForUser(Long userid, Order order) throws UserNotFoundException {
        User user = findUser(userid);
        user.addOrder(order);
		userRepository.saveAndFlush(user);
		
		return orderRepository.findByUserId(userid).stream()
				.filter(o -> o.getIsbn().equals(order.getIsbn()) && o.getExpiry().equals(order.getExpiry()))
				.findFirst()
				.orElse(order);
	}
}
