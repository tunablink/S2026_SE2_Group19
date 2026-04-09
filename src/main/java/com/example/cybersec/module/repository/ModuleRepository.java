package com.example.cybersec.module.repository;

import com.example.cybersec.module.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleRepository extends JpaRepository<Module, Long> {
}
