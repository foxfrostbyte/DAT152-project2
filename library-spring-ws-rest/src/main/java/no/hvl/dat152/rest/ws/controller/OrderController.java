package no.hvl.dat152.rest.ws.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.service.OrderService;

/**
 * REST API controller for orders
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class OrderController {

	@Autowired
	private OrderService orderService;

 	@GetMapping("/orders")
    public ResponseEntity<Object> getAllBorrowOrders(
            @RequestParam(required = false) String expiry,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<Order> orders;

        if (expiry != null && !expiry.isBlank()) {
            LocalDate expiryDate = LocalDate.parse(expiry);
            orders = orderService.findByExpiryDate(expiryDate, pageable);
        } else {
            orders = orderService.findAllOrders();
        }

        if (orders.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

 	@GetMapping("/orders/{id}")
    public ResponseEntity<Object> getBorrowOrder(@PathVariable Long id)
            throws OrderNotFoundException {

        Order order = orderService.findOrder(id);

        order.add(linkTo(methodOn(OrderController.class)
                .getBorrowOrder(id))
                .withSelfRel());
        order.add(linkTo(methodOn(OrderController.class)
                .getAllBorrowOrders(null, 0, 10))
                .withRel("allOrders"));

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

   @PutMapping("/orders/{id}")
    public ResponseEntity<Object> updateOrder(
            @PathVariable Long id,
            @RequestBody Order order)
            throws OrderNotFoundException {

        Order updatedOrder = orderService.updateOrder(order, id);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Object> deleteBookOrder(@PathVariable Long id)
            throws OrderNotFoundException {

        orderService.deleteOrder(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
