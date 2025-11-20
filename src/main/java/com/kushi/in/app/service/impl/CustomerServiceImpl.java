// src/main/java/com/kushi/in/app/service/impl/CustomerServiceImpl.java
package com.kushi.in.app.service.impl;

import com.kushi.in.app.dao.CustomerRepository;

import com.kushi.in.app.dao.ServiceRepository;
import com.kushi.in.app.entity.Customer;
import com.kushi.in.app.entity.Services;
import com.kushi.in.app.model.CustomerDTO;
import com.kushi.in.app.service.CustomerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private final CustomerRepository customerRepository; // lowercase 'c'
    private final ServiceRepository serviceRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository, ServiceRepository serviceRepository) {
        this.customerRepository = customerRepository;
        this.serviceRepository = serviceRepository;
    }


    // ===========================
    // Customer Management
    // ===========================

    @Override
    public List<CustomerDTO> getAll() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Customer getById(Long bookingId) {
        return customerRepository.findById(bookingId).orElse(null);
    }

    @Override
    public Customer create(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer update(Long bookingId, Customer update) {
        return customerRepository.findById(bookingId).map(c -> {
            c.setCustomer_name(update.getCustomer_name());
            c.setCustomer_email(update.getCustomer_email());
            c.setCustomer_number(update.getCustomer_number());
            c.setAddress_line_1(update.getAddress_line_1());
            c.setCity(update.getCity());
            c.setZip_code(update.getZip_code());
            c.setBooking_service_name(update.getBooking_service_name());
            c.setBookingStatus(update.getBookingStatus());
            c.setPayment_status(update.getPayment_status());
            c.setPayment_method(update.getPayment_method());
            c.setRemarks(update.getRemarks());
            c.setSite_visit(update.getSite_visit());
            return customerRepository.save(c);

        }).orElse(null);
    }

    @Override
    public boolean delete(Long bookingId) {
        if (customerRepository.existsById(bookingId)) {
            customerRepository.deleteById(bookingId);
            return true;
        }
        return false;
    }

    @Override
    public List<CustomerDTO> getOrdersByUserId(Long userId) {
        return customerRepository.findByUser_Id(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerDTO> getLoggedInCustomers() {
        return customerRepository.findByUserIsNotNull()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerDTO> getGuestCustomers() {
        return customerRepository.findByUserIsNull()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerDTO> getCompletedBookings() {
        return customerRepository.findByBookingStatus("Completed")
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Customer> getCustomersByBookingStatus(String status) {
        return customerRepository.findByBookingStatus(status);
    }

    // ===========================
    // Service Management
    // ===========================

    @Override
    public Services addService(Services services) {
        return serviceRepository.save(services);
    }

    @Override
    public List<Services> getAllServices() {
        return serviceRepository.findAll();
    }

    @Override
    public void deleteService(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new RuntimeException("Service with ID " + id + " not found");
        }
        serviceRepository.deleteById(id);
    }


    @Override
    public Services updateService(Long id, Services services) {
        Services existing = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id " + id));

        // Only update fields that are sent/valid
        if (services.getService_name() != null) existing.setService_name(services.getService_name());
        if (services.getService_type() != null) existing.setService_type(services.getService_type());
        if (services.getService_category() != null) existing.setService_category(services.getService_category());

        // For double (primitive), only update if > 0 (or some other business logic)
        if (services.getService_cost() > 0) existing.setService_cost(services.getService_cost());

        if (services.getService_description() != null) existing.setService_description(services.getService_description());
        if (services.getService_details() != null) existing.setService_details(services.getService_details());
        if (services.getService_image_url() != null) existing.setService_image_url(services.getService_image_url());
        if (services.getRating() != null) existing.setRating(services.getRating());
        if (services.getRating_count() != null) existing.setRating_count(services.getRating_count());
        if (services.getRemarks() != null) existing.setRemarks(services.getRemarks());
        if (services.getActive() != null) existing.setActive(services.getActive());
        if (services.getUpdated_by() != null) existing.setUpdated_by(services.getUpdated_by());
        if (services.getUpdated_date() != null) existing.setUpdated_date(services.getUpdated_date());
        if (services.getService_package() != null) existing.setService_package(services.getService_package());

        // Large text fields
        if (services.getOverview() != null) existing.setOverview(services.getOverview());
        if (services.getOur_process() != null) existing.setOur_process(services.getOur_process());
        if (services.getBenefits() != null) existing.setBenefits(services.getBenefits());
        if (services.getWhats_included() != null) existing.setWhats_included(services.getWhats_included());
        if (services.getWhats_not_included() != null) existing.setWhats_not_included(services.getWhats_not_included());
        if (services.getWhy_choose_us() != null) existing.setWhy_choose_us(services.getWhy_choose_us());

        if (services.getKushi_teamwork() != null) existing.setKushi_teamwork(services.getKushi_teamwork());
        if (services.getFaq() != null) existing.setFaq(services.getFaq());
        return serviceRepository.save(existing);
    }



    // ===========================
    // Helper Methods
    // ===========================

    private CustomerDTO mapToDTO(Customer c) {
        return new CustomerDTO(
                c.getBooking_id(),                        // Long -> Long
                c.getCustomer_id(),                       // Integer -> Integer
                c.getUser() != null ? c.getUser().getId() : null, // Long -> Long
                c.getCustomer_name(),
                c.getCustomer_email(),
                c.getCustomer_number(),
                c.getAddress_line_1(),
                c.getCity(),
                c.getTotalAmount(),
                c.getBookingDate(),
                c.getBookingStatus(),
                c.getBooking_time(),
<<<<<<< HEAD
                c.getBooking_service_name(),
                c.getGrand_total() != null ? c.getGrand_total() : c.getBooking_amount(), // Use grand_total if discount applied
                c.getDiscount(),
                c.getGrand_total(),
                c.getPayment_method(),
                c.getPayment_status()
=======
                c.getBooking_service_name(), // service name goes here
                c.getBooking_amount() ,       // booking amount goes last
                c.getCustomer_id()

>>>>>>> f0144ebd8f89dd88c5fff2bf7939a03f55b7b788
        );
    }



    // Helper: ignore null fields when copying
    private String[] getNullPropertyNames(Services source) {
        return java.util.Arrays.stream(source.getClass().getDeclaredFields())
                .filter(f -> {
                    try {
                        f.setAccessible(true);
                        return f.get(source) == null;
                    } catch (IllegalAccessException e) {
                        return false;
                    }
                })
                .map(f -> f.getName())
                .toArray(String[]::new);
    }




    @Override
    public String updateServiceStatus(Long id, String status) {
        Optional<Services> serviceOpt = serviceRepository.findById(id);
        if (serviceOpt.isPresent()) {
            Services service = serviceOpt.get();
            // save Y = enabled, N = disabled
            service.setActive(status.equals("Y") ? "Y" : "N");
            serviceRepository.save(service);
            return "Success";
        }
        return "Failed";
    }
}
