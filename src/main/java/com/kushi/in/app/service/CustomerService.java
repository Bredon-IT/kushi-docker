package com.kushi.in.app.service;

import com.kushi.in.app.entity.Customer;
import com.kushi.in.app.entity.Services;
import com.kushi.in.app.model.CustomerDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CustomerService {
    Services addService(Services services);

    List<Services> getAllServices();

    void deleteService(Long id);


    Services updateService(Long id, Services services);

    List<CustomerDTO> getAll();
    Customer getById(Long bookingId);
    Customer create(Customer customer);
    Customer update(Long bookingId, Customer update);
    boolean delete(Long bookingId);

    List<CustomerDTO> getOrdersByUserId(Long userId);
    List<Customer> getCustomersByBookingStatus(String status);

    List<CustomerDTO> getLoggedInCustomers();
    List<CustomerDTO> getGuestCustomers();
    List<CustomerDTO> getCompletedBookings();




    // Update status (Enable/Disable)
    String updateServiceStatus(Long serviceId, String status);




}