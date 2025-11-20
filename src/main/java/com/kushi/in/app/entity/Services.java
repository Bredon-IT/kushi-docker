package com.kushi.in.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name="tbl_service_info" )
public class Services {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long service_id;

    private String active;

    private String create_date;

    private String created_by;

    private String rating;

    private String rating_count;

    private String remarks;

    private double service_cost;

    @Column(columnDefinition = "LONGTEXT")
    private String service_description;

    private String service_details;

    private String service_image_url;

    private String service_name;

    private String service_type;

    private String updated_by;

    private String updated_date;
    @Column(columnDefinition = "LONGTEXT")
    private String service_package;

    private String service_category;

    public String getService_package() {
        return service_package;
    }

    public void setService_package(String service_package) {
        this.service_package = service_package;
    }

    @Override
    public String toString() {
        return "Services{" +
                "service_id=" + service_id +
                ", active='" + active + '\'' +
                ", create_date='" + create_date + '\'' +
                ", created_by='" + created_by + '\'' +
                ", rating='" + rating + '\'' +
                ", rating_count='" + rating_count + '\'' +
                ", remarks='" + remarks + '\'' +
                ", service_cost=" + service_cost +
                ", service_description='" + service_description + '\'' +
                ", service_details='" + service_details + '\'' +
                ", service_image_url='" + service_image_url + '\'' +
                ", service_name='" + service_name + '\'' +
                ", service_type='" + service_type + '\'' +
                ", updated_by='" + updated_by + '\'' +
                ", updated_date='" + updated_date + '\'' +
                ", service_package='" + service_package + '\'' +
                ", service_category='" + service_category + '\'' +
                ", overview='" + overview + '\'' +
                ", our_process='" + our_process + '\'' +
                ", benefits='" + benefits + '\'' +
                ", whats_included='" + whats_included + '\'' +
                ", whats_not_included='" + whats_not_included + '\'' +
                ", why_choose_us='" + why_choose_us + '\'' +
                ", kushi_teamwork='" + kushi_teamwork + '\'' +
                ", faq='" + faq + '\'' +
                '}';
    }

    @Column(columnDefinition = "LONGTEXT")
    private String overview;
    @Column(columnDefinition = "LONGTEXT")
    private String our_process;
    @Column(columnDefinition = "LONGTEXT")
    private String benefits;
    @Column(columnDefinition = "LONGTEXT")
    private String whats_included;
    @Column(columnDefinition = "LONGTEXT")
    private String whats_not_included;
    @Column(columnDefinition = "LONGTEXT")
    private String why_choose_us;


    @Column(columnDefinition = "LONGTEXT")
    private String kushi_teamwork;
    @Column(columnDefinition = "LONGTEXT")
    private String faq;

    public String getOverview() {
        return overview;
    }

    public String getWhy_choose_us() {
        return why_choose_us;
    }

    public void setWhy_choose_us(String why_choose_us) {
        this.why_choose_us = why_choose_us;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getOur_process() {
        return our_process;
    }

    public void setOur_process(String our_process) {
        this.our_process = our_process;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getWhats_included() {
        return whats_included;
    }

    public void setWhats_included(String whats_included) {
        this.whats_included = whats_included;
    }

    public String getWhats_not_included() {
        return whats_not_included;
    }

    public void setWhats_not_included(String whats_not_included) {
        this.whats_not_included = whats_not_included;
    }

    public Long getService_id() {
        return service_id;
    }

    public void setService_id(Long id) {
        this.service_id = id; // ✅ correctly sets the service_id
    }


    public String getActive() {
        return active;
    }

    public String getService_category() {
        return service_category;
    }

    public void setService_category(String service_category) {
        this.service_category = service_category;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRating_count() {
        return rating_count;
    }

    public void setRating_count(String rating_count) {
        this.rating_count = rating_count;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public double getService_cost() {
        return service_cost;
    }

    public void setService_cost(double service_cost) {
        this.service_cost = service_cost;
    }

    public String getService_description() {
        return service_description;
    }

    public void setService_description(String service_description) {
        this.service_description = service_description;
    }

    public String getService_details() {
        return service_details;
    }

    public void setService_details(String service_details) {
        this.service_details = service_details;
    }

    public String getService_image_url() {
        return service_image_url;
    }

    public void setService_image_url(String service_image_url) {
        this.service_image_url = service_image_url;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public String getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(String updated_date) {
        this.updated_date = updated_date;
    }

    public String getKushi_teamwork() {
        return kushi_teamwork;
    }

    public void setKushi_teamwork(String kushi_teamwork) {
        this.kushi_teamwork = kushi_teamwork;
    }

    public String getFaq() {
        return faq;
    }

    public void setFaq(String faq) {
        this.faq = faq;
    }
}
