package com.kushi.in.app.service.impl;

import com.kushi.in.app.dao.CategoryWiseBookingRepository;
import com.kushi.in.app.model.CategoryWiseBookingDTO;
import com.kushi.in.app.service.CategoryWiseBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryWiseBookingServiceImpl implements CategoryWiseBookingService {

    @Autowired
    private CategoryWiseBookingRepository categoryWiseBookingRepository;

    @Override
    public List<CategoryWiseBookingDTO> getCategoryWiseBookings() {
        return categoryWiseBookingRepository.getCategoryWiseBookings();
    }
}
