package com.example.cmd.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date = new Date();
    @ManyToOne
    private StatuCommande statu;
    @ManyToMany
    @JoinTable(
            name = "commande_produit",
            joinColumns = @JoinColumn(
                    name = "commande_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "produit_id"
            )
    )

    private List<Produit> produits = new ArrayList<>();

    @OneToOne(mappedBy = "commande")
    private Livraison livraison;

    public void setProduit(List<Produit> produitsList) {
    }
}
