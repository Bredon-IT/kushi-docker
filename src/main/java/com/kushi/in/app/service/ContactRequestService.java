package com.kushi.in.app.service;

import com.kushi.in.app.dao.ContactRequestRepository;
import com.kushi.in.app.entity.ContactRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactRequestService {

    @Autowired
    private ContactRequestRepository repository;

    public void save(ContactRequest request) {
        repository.save(request);
    }

    public List<ContactRequest> getAllRequests() {
        return repository.getAllRequests();
    }

    public List<ContactRequest> getReadRequests() {
        return repository.findByIsReadOrderBySubmittedAtDesc(true);
    }

    public List<ContactRequest> getUnreadRequests() {
        return repository.findByIsReadOrderBySubmittedAtDesc(false);
    }

    public boolean markAsRead(Long id) {
        Optional<ContactRequest> opt = repository.findById(id);
        if (opt.isPresent()) {
            ContactRequest cr = opt.get();
            cr.setRead(true);
            repository.save(cr);
            return true;
        }
        return false;
    }

    public boolean markAsUnread(Long id) {
        Optional<ContactRequest> opt = repository.findById(id);
        if (opt.isPresent()) {
            ContactRequest cr = opt.get();
            cr.setRead(false);
            repository.save(cr);
            return true;
        }
        return false;
    }
}
