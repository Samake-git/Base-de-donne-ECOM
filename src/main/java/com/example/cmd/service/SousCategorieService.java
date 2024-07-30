package com.example.cmd.service;

import com.example.cmd.model.Category;
import com.example.cmd.model.SousCategory;
import com.example.cmd.repository.CategoryRepository;
import com.example.cmd.repository.SousCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class SousCategorieService {

    private SousCategoryRepository sousCategoryRepository;
    private CategoryService categoryService;

    public List<SousCategory> getAllSousCategory() {
        return this.sousCategoryRepository.findAll();
    }

    public SousCategory getCategory(Long id) {
        return this.sousCategoryRepository.findById(id).orElse(null);
    }

    public SousCategory getCategoryBy(String libelle) {
        return this.sousCategoryRepository.findByLibelle(libelle);
    }

    public SousCategory createSousCategory(SousCategory sousCategory) {
        SousCategory sousCat = new SousCategory();
        Category category = categoryService.getCategory(sousCategory.getCategory().getId());
        sousCat.setLibelle(sousCategory.getLibelle());
        sousCat.setCategory(category);
        return this.sousCategoryRepository.save(sousCat);
    }

    public SousCategory updateCategory(Long id, String libelle) {
        SousCategory sousCategory = this.sousCategoryRepository.findById(id).orElse(null);
        if (sousCategory != null) {
            sousCategory.setLibelle(libelle);
            return this.sousCategoryRepository.save(sousCategory);
        }
        return null;
    }

    public void deleteSousCategory(Long id) {
        this.sousCategoryRepository.deleteById(id);
    }
}
