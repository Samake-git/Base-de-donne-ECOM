package com.example.cmd.controller;

import com.example.cmd.DTO.AvisDTO;
import com.example.cmd.DTO.ChangePasswordDto;
import com.example.cmd.DTO.CreateClientDto;
import com.example.cmd.config.CustomUserPrincipal;
import com.example.cmd.model.*;
import com.example.cmd.service.AvisService;
import com.example.cmd.service.ClientService;
import com.example.cmd.service.PanierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private PanierService panierService;

    @Autowired
    private AvisService avisService;

    public ClientController(ClientService clientService, AvisService avisService, PanierService panierService) {
        this.clientService = clientService;
        this.avisService = avisService;
        this.panierService = panierService;
    }

    @GetMapping("/{clientId}/profil")
    public Object afficherProfil(@PathVariable Long clientId) {
        if (!clientService.estCompteActif(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé : Compte désactivé.");
        }
        return "Votre compte est activé";
    }

    @PostMapping("/{clientId}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Long clientId, @RequestBody ChangePasswordDto changePasswordDto) {
        try {
            clientService.changePassword(clientId, changePasswordDto);
            return ResponseEntity.ok().build(); // Retourne une réponse HTTP 200 OK si le changement de mot de passe réussit
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Retourne une réponse HTTP 400 Bad Request si l'ancien mot de passe est incorrect
        }
    }

    private Client convertirDtoEnEntite(CreateClientDto dto) {
        Client client = new Client();
        client.setNom(dto.getNom());
        client.setPrenom(dto.getPrenom());
        client.setEmail(dto.getEmail());
        client.setMotDePasse(dto.getMotDePasse()); // Assurez-vous de hasher le mot de passe avant de l'enregistrer
        client.setAdresse(dto.getAdresse());
        client.setTelephone(dto.getTelephone());
        client.setStatus(StatusCompte.ACTIVE); // Définissez le statut par défaut comme actif
        return client;
    }

    // Endpoint pour les Avis

    @PostMapping("/CreerAvis")
    public ResponseEntity<Avis> createAvis(@RequestBody AvisDTO avisDTO) {
        Long clientId = getAuthenticatedClientId();
        Avis createdAvis = avisService.createAvis(avisDTO, clientId);
        return new ResponseEntity<>(createdAvis, HttpStatus.CREATED);
    }

    private Long getAuthenticatedClientId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserPrincipal) {
                CustomUserPrincipal customPrincipal = (CustomUserPrincipal) principal;
                if (customPrincipal.getClient() != null) {
                    return customPrincipal.getClient().getId();
                } else {
                    throw new IllegalArgumentException("Authenticated user is not a client");
                }
            }
            throw new IllegalArgumentException("Principal is not an instance of CustomUserPrincipal");
        }
        throw new IllegalArgumentException("User is not authenticated");
    }

    @PutMapping("/modifierAvis/{id}")
    public ResponseEntity<Avis> updateAvis(@PathVariable Long id, @RequestBody Avis avisDetails) {
        Avis updatedAvis = avisService.modifierAvis(id, avisDetails);
        return new ResponseEntity<>(updatedAvis, HttpStatus.OK);
    }

    @DeleteMapping("/SupprimerAvis/{id}")
    public ResponseEntity<String> deleteAvis(@PathVariable Long id) {
        String message = avisService.supprimerAvis(id);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // Ajouter le prodit dans le panier
    @PostMapping("/{clientId}/panier/ajouterProduit")
    public ResponseEntity<Panier> ajouterProduitAuPanier(@PathVariable Long clientId, @RequestParam Long produitId, @RequestParam int quantite) {
        Panier panier = panierService.ajouterProduitAuPanier(clientId, produitId, quantite);
        return new ResponseEntity<>(panier, HttpStatus.OK);
    }


    // Modifier la quantité du produit dans le panier
    @PutMapping("/{clientId}/panier/{panierId}/modifierQuantite")
    public ResponseEntity<Panier> modifierQuantiteProduitDansPanier(@PathVariable Long clientId, @PathVariable Long panierId, @RequestParam Long produitId, @RequestParam int nouvelleQuantite) {
        Panier panier = panierService.modifierQuantiteProduitDansPanier(panierId, produitId, nouvelleQuantite);
        return new ResponseEntity<>(panier, HttpStatus.OK);
    }

    // Supprimer le produit dans le panier
    @DeleteMapping("/{clientId}/panier/{panierId}/supprimerProduit")
    public ResponseEntity<Panier> supprimerProduitDuPanier(@PathVariable Long clientId, @PathVariable Long panierId, @RequestParam Long produitId) {
        Panier panier = panierService.supprimerProduitDuPanier(panierId, produitId);
        return new ResponseEntity<>(panier, HttpStatus.OK);
    }

    // Supprimer son panier
    @DeleteMapping("/{clientId}/panier/{panierId}")
    public ResponseEntity<String> supprimerPanier(@PathVariable Long clientId, @PathVariable Long panierId) {
        String message = panierService.supprimerPanier(panierId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }



    // Afficher tous les produits dans le Panier

    @GetMapping("/{panierId}/produits")
    public ResponseEntity<List<Produit>> getAllProduitsInPanier(@PathVariable Long panierId) {
        List<Produit> produits = panierService.getAllProduitsInPanier(panierId);
        return new ResponseEntity<>(produits, HttpStatus.OK);
    }


    // Afficher un produit dans le panier par son ID

    @GetMapping("/{panierId}/produits/{produitId}")
    public ResponseEntity<Produit> getProduitByIdInPanier(@PathVariable Long panierId, @PathVariable Long produitId) {
        Produit produit = panierService.getProduitByIdInPanier(panierId, produitId);
        if (produit != null) {
            return new ResponseEntity<>(produit, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Payé tous les produits dans le panier
    @PostMapping("/payer/panier/{panierId}")
    public ResponseEntity<String> payerProduitsDansPanier(@PathVariable Long panierId, @RequestParam float montantClient) {
        String resultat = panierService.payerProduitsDansPanier(panierId, montantClient);
        return new ResponseEntity<>(resultat, HttpStatus.OK);
    }

}
