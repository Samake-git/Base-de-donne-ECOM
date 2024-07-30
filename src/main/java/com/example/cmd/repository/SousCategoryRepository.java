package com.example.cmd.repository;

import com.example.cmd.model.SousCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SousCategoryRepository extends JpaRepository<SousCategory, Long> {
    SousCategory findByLibelle(String libelle);
}
