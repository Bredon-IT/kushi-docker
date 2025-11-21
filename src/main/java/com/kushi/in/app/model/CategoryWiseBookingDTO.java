// src/main/java/com/kushi/in/app/model/CategoryWiseBookingDTO.java
package com.kushi.in.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryWiseBookingDTO {
    private String serviceCategory;
    private Long completedCount;
    private Long cancelledCount;
}
