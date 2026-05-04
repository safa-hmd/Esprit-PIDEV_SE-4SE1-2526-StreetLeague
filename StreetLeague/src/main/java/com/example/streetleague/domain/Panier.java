package com.example.streetleague.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})


//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
@Builder
@Table(name = "paniers")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Panier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
            @JsonIgnore
    User user;

    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL)
            @JsonIgnore
    List<LignePanier> lignes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<LignePanier> getLignes() {
        return lignes;
    }

    public void setLignes(List<LignePanier> lignes) {
        this.lignes = lignes;
    }

    public Panier(Long id, User user, List<LignePanier> lignes) {
        this.id = id;
        this.user = user;
        this.lignes = lignes;
    }

    public Panier() {
    }
}