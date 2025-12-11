package com.kushi.in.app.controller;

import com.kushi.in.app.entity.ContactRequest;
import com.kushi.in.app.service.ContactRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.kushi.in.app.config.AppConstants.*;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = {"https://dev.dhtawzq4yzgjo.amplifyapp.com"}) // {KUSHI_SERVICES_URL, KUSHI_SERVICES_WWW_URL})
public class ContactRequestController {

    @Autowired
    private ContactRequestService service;

    @PostMapping("/submit")
    public ResponseEntity<String> submitContact(@RequestBody ContactRequest request) {
        service.save(request);
        return ResponseEntity.ok("Saved");
    }

    @GetMapping("/all")
    public ResponseEntity<List<ContactRequest>> getAll() {
        return ResponseEntity.ok(service.getAllRequests());
    }

    @GetMapping("/read")
    public ResponseEntity<List<ContactRequest>> getRead() {
        return ResponseEntity.ok(service.getReadRequests());
    }

    @GetMapping("/unread")
    public ResponseEntity<List<ContactRequest>> getUnread() {
        return ResponseEntity.ok(service.getUnreadRequests());
    }

    @PutMapping("/mark-read/{id}")
    public ResponseEntity<String> markRead(@PathVariable Long id) {
        boolean ok = service.markAsRead(id);
        if (ok) return ResponseEntity.ok("Marked read");
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/mark-unread/{id}")
    public ResponseEntity<String> markUnread(@PathVariable Long id) {
        boolean ok = service.markAsUnread(id);
        if (ok) return ResponseEntity.ok("Marked unread");
        return ResponseEntity.notFound().build();
    }
}
