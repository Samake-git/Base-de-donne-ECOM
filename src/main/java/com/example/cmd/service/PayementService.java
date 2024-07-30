package com.example.cmd.service;

import com.example.cmd.model.Commande;
import com.example.cmd.model.Facture;
import com.example.cmd.model.Payement;
import com.example.cmd.model.Recu;
import com.example.cmd.repository.FactureRepository;
import com.example.cmd.repository.PayementRepository;
import com.example.cmd.repository.RecuRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PayementService {
    private PayementRepository payementRepository;
    private RecuRepository recuRepository;
    private CommandeService commandeService;
    private FactureRepository factureRepository;

    @Transactional
    public Payement effectuerPayement(Commande commande) {
        Commande commande1 = this.commandeService.getCommande(commande.getId());
        Payement payement = new Payement();
        payement.setCommande(commande1);
        Facture facture = this.factureRepository.findByCommande(commande1);
        if (facture == null) {
            throw new RuntimeException("facture non trouvable");
        }
        payement.setMontant(facture.getTotal());
        Payement payementSaved = this.payementRepository.save(payement);
        Recu recu = new Recu();
        recu.setPayement(payementSaved);
        recu.setTotal(facture.getTotal());
        this.recuRepository.save(recu);
        return payementSaved;
    }

    public List<Payement> recupererPayements() {
        return this.payementRepository.findAll();
    }
}
