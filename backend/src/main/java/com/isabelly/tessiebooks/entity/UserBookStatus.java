package com.isabelly.tessiebooks.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "user_book_status")
public class UserBookStatus {

    @Column(name = "paginas_lidas")
private Integer paginasLidas = 0;

@Column(name = "paginas_totais")
private Integer paginasTotais = 0;

@Column(name = "favorito")
private Boolean favorito = false;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Enumerated(EnumType.STRING)
    private ReadingStatus status;

    private Integer currentPage;
    private Integer rating; // 1–5 estrelas
    private String reviewText;

    private LocalDate startDate;
    private LocalDate finishDate;

    // Dentro da classe UserBookStatus.java


// Verifique se você já tem os getters e setters ou use o @Data do Lombok
}
