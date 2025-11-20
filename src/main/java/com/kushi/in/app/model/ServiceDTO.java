
package com.kushi.in.app.model;

public class ServiceDTO {

    private Long rating_count;

    private String service_image_url;

    private String service_name;

    private Double rating;

    private Double service_cost;

    private String service_type;

    public ServiceDTO(String service_name, Double rating, Long rating_count,
                      String service_type, Double service_cost, String service_image_url) {
        this.service_name = service_name;
        this.rating = rating;
        this.rating_count = rating_count;
        this.service_type = service_type;
        this.service_cost = service_cost;
        this.service_image_url = service_image_url;
    }


    public Long getRating_count() {
        return rating_count;
    }

    public void setRating_count(Long rating_count) {
        this.rating_count = rating_count;
    }

    public String getService_image_url() {
        return service_image_url;
    }

    public void setService_image_url(String service_image_url) {
        this.service_image_url = service_image_url;
    }

    public Double getService_cost() {
        return service_cost;
    }

    public void setService_cost(Double service_cost) {
        this.service_cost = service_cost;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }



    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }



}


