package com.example.cybersec.module.repository;

import com.example.cybersec.module.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    Optional<Module> findByName(String name);
}
