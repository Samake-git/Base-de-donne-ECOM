package com.example.cmd.service;

import com.example.cmd.model.TypeLivraison;
import com.example.cmd.repository.TypeLivraisonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TypeLivraisonService {

    @Autowired
    private TypeLivraisonRepository typeLivraisonRepository;


    public TypeLivraison ajouterTypeLivraison(TypeLivraison typeLivraison) {
        return typeLivraisonRepository.save(typeLivraison);
    }

    public TypeLivraisonService(TypeLivraisonRepository typeLivraisonRepository) {
        this.typeLivraisonRepository = typeLivraisonRepository;
    }

    public Optional<TypeLivraison> findById(Long id) {
        return typeLivraisonRepository.findById(id);
    }
}
