package com.example.cmd.service;

import com.example.cmd.model.Commande;
import com.example.cmd.model.Facture;
import com.example.cmd.model.Produit;
import com.example.cmd.model.StatuCommande;
import com.example.cmd.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommandeService {
    private final CommandeRepository commandeRepository;
    private final FactureRepository factureRepository;
    private final ProduitRepository produitRepository;
    private final StatuCommandeService statuCommandeService;
    private StatuCommandeRepository statuCommandeRepository;
    @Autowired
    public CommandeService(StatuCommandeRepository statuCommandeRepository, CommandeRepository commandeRepository, FactureRepository factureRepository, ProduitRepository produitRepository, StatuCommandeService statuCommandeService) {
        this.commandeRepository = commandeRepository;
        this.statuCommandeRepository =  statuCommandeRepository;
        this.factureRepository = factureRepository;
        this.produitRepository = produitRepository;
        this.statuCommandeService = statuCommandeService;
    }
    @Transactional
    public Commande saveCommande(Commande commande) {
        return this.commandeRepository.save(commande);
    }
    @Transactional
    public Commande passerCommande(List<Produit> produits) {
        Commande commande = new Commande();
        float total = 0;
        List<Produit> produitsList = new ArrayList<>();

        for (Produit p : produits) {
            total += p.getPrix() * p.getQuantite();
            Produit produit = this.produitRepository.findById(p.getId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
            produitsList.add(produit);
        }

        commande.setProduit(produitsList);


        StatuCommande statutEnAttente = this.statuCommandeService.recupererStatusCommande("en_attente");
        commande.setStatu(statutEnAttente);
        Facture facture = new Facture();
        facture.setTotal(total);
        facture.setCommande(commande);
        this.factureRepository.save(facture);
        return this.commandeRepository.save(commande);
    }

    public Optional<Commande> findById(long id) {
        return this.commandeRepository.findById(id);
    }

    public boolean verifierStatutExiste(String statutNom) {
        // Utilise Optional pour vérifier la présence du statut
        return statuCommandeRepository.findByLibelle(statutNom).isPresent();
    }

    public String changerStatut(Long commandeId, String statutNom) {
        Optional<StatuCommande> statutOptional = statuCommandeRepository.findByLibelle(statutNom);
        if (!statutOptional.isPresent()) {
            return "Le statut '" + statutNom + "' n'existe pas";
        }
        StatuCommande statut = statutOptional.get();

        Optional<Commande> commandeOptional = commandeRepository.findById(commandeId);
        if (!commandeOptional.isPresent()) {
            return "Commande non trouvée";
        }

        Commande commande = commandeOptional.get();
        commande.setStatu(statut);

        // Sauvegarder les modifications
        commandeRepository.save(commande);

        return "Statut de la commande mis à jour avec succès";
    }
    public Commande getCommande(long id) {
        return this.commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
    }

    @Transactional
    public String changerStatut(long commandeId, String statutNom) {
        Commande commande = this.commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        StatuCommande statutCommande = this.statuCommandeService.recupererStatusCommande(statutNom);

        commande.setStatu(statutCommande);
        this.commandeRepository.save(commande);

        return "Statut modifié avec succès";
    }

    public List<Commande> getCommandes() {
        return this.commandeRepository.findAll();
    }


}
