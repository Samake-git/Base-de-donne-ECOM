package com.example.cmd.service;
import com.example.cmd.model.Produit;
import com.example.cmd.model.Stock;
import com.example.cmd.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class StockServiceImpl implements StockService{
    private StockRepository stockRepository;

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void ajouterProduit(Produit p) {

        Stock stockInDB = new Stock();
        stockInDB.setProduit(p);
        stockInDB.setQuantite(p.getQuantite());

        // Sauvegardez le stock dans la base de données
        this.stockRepository.save(stockInDB);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void supprimerProduit(Produit p) {
        Stock stock = this.stockRepository.findByProduit(p).get();
        this.stockRepository.delete(stock);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void retirerProduit(Produit produit, int quantite) {
        Optional<Stock> stock = this.stockRepository.findByProduit(produit);
        if (stock.isPresent()) {
            Stock stockInDB = stock.get();
            int qte = stockInDB.getQuantite();
            if (qte > 0 && quantite<qte) {
                stockInDB.setQuantite(qte - quantite);
                this.stockRepository.save(stockInDB);
            } else {
                System.out.println("Quantite insuffisante !!!");
            }
        } else {
            System.out.println("Ce produit n'existe pas !!!");
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void incrementer(Produit p, int quantiteToAdd) {
        Stock stock = this.stockRepository.findByProduit(p).get();
        Produit produit = stock.getProduit();
        stock.setQuantite(quantiteToAdd + produit.getQuantite());
        this.stockRepository.save(stock);
    }
}
