package com.example.cmd.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name="client")
public class Client extends Utilisateur {

    private String nom;
    private String prenom;
    private String adresse;
    private String telephone;

    @Enumerated(EnumType.STRING)
    private StatusCompte status;



}


