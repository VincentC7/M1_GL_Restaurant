package fr.ul.miage.groupe7.projetrestaurant.Database;

import java.time.LocalDate;
import java.util.Objects;

public class Reservation {

    String nom;
    CRENEAU creneau;
    LocalDate date;

    public enum CRENEAU{

        SOIR("Soir"),
        MATIN("Matin");

        private final String creneau;

        CRENEAU(final String creneau) {
            this.creneau = creneau;
        }

        @Override
        public String toString() {
            return creneau;
        }
    }

    public Reservation(String creneau, String nom, LocalDate date) {
        setCreneau(creneau);
        this.nom = nom;
        this.date = date;
    }

    public Reservation(CRENEAU creneau, String nom, LocalDate date) {
        this.creneau = creneau;
        this.nom = nom;
        this.date = date;
    }

    public String getCreneau() {
        return creneau.toString();
    }

    public String getNom() {
        return nom;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setCreneau(String creneau) {
        if(CRENEAU.MATIN.toString().equals(creneau)){
            this.creneau = CRENEAU.MATIN;
        }
        else if(CRENEAU.SOIR.toString().equals(creneau)){
            this.creneau = CRENEAU.SOIR;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return creneau.equals(that.creneau) && date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creneau, date);
    }
}
