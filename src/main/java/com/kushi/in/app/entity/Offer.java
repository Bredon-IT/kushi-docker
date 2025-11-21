package com.kushi.in.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_offers")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text = "";

    @Column(name = "image_url")
    private String imageUrl;

    private String fontFamily;
    private String color;
    private String emoji;

    // banner-specific fields
    private String title;
    private String link;

    // Constructors, getters, setters
    public Offer() {}

    public Offer(String text, String imageUrl) { this.text = text; this.imageUrl = imageUrl; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }


    public String getText() { return text; }
    public void setText(String text) { this.text = (text == null ? "" : text); }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getFontFamily() { return fontFamily; }
    public void setFontFamily(String fontFamily) { this.fontFamily = fontFamily; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
}

