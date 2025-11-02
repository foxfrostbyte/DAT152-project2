package no.hvl.dat152.rest.ws.service;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.repository.OrderRepository;
import no.hvl.dat152.rest.ws.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service layer for orders
 */
@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	public Order saveOrder(Order order) {
		order = orderRepository.save(order);
		return order;
	}
	
	public Order findOrder(Long id) throws OrderNotFoundException {
        return orderRepository.findById(id)
                .orElseThrow(()-> new OrderNotFoundException("Order with id: "+id+" not found in the order list!"));
	}

    public void deleteOrder(Long id) throws OrderNotFoundException {
        if (!orderRepository.existsById(id)) {
            return;
        }
        
        findOrder(id);
        Long userId = orderRepository.findUserID(id);
        
        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                user.getOrders().size();
                user.getOrders().removeIf(o -> o.getId() != null && o.getId().equals(id));
                userRepository.saveAndFlush(user);
            }
        }
        orderRepository.deleteById(id);
    }

	public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }
    
    public List<Order> findAllOrders(Pageable pageable) {
        Page<Order> page = orderRepository.findAll(pageable);
        return page.getContent();
    }

    public List<Order> findByExpiryDate(LocalDate expiry, Pageable page) {
        Page<Order> result = orderRepository.findByExpiryBefore(expiry, page);
        return result.getContent();
    }

	public Order updateOrder(Order order, Long id) throws OrderNotFoundException {
		Order existingOrder = findOrder(id);

		if (!existingOrder.getIsbn().equals(order.getIsbn())) {
			Order existingOrderWithIsbn = orderRepository.findAll().stream()
					.filter(o -> o.getIsbn().equals(order.getIsbn()) && !o.getId().equals(id))
					.findFirst()
					.orElse(null);
			if (existingOrderWithIsbn != null) {
				existingOrder.setExpiry(order.getExpiry());
				return orderRepository.save(existingOrder);
			}
			existingOrder.setIsbn(order.getIsbn());
		}
		existingOrder.setExpiry(order.getExpiry());
		return orderRepository.save(existingOrder);
	}

}
