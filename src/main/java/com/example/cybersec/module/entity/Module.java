package com.example.cybersec.module.entity;

import jakarta.persistence.*;

/**
 * JPA Entity đại diện cho một học phần (Module) trong hệ thống OWASP.
 * <p>
 * Mỗi module tương ứng với một chủ đề bảo mật (A01–A10) và chứa
 * tiêu đề, tên định danh, và mô tả nội dung.
 * </p>
 */
@Entity
@Table(name = "modules")
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String name;

    @Column(length = 1024)
    private String description;

    public Module() {}

    public Module(String title, String name, String description) {
        this.title = title;
        this.name = name;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
