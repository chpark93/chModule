package com.ch.catalog.domain;

import com.ch.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

/**
 * TABLE catalog
 * Catalog
 */
@Getter @Setter
@ToString
@Entity
@NoArgsConstructor
@Table(name="catalog")
public class Catalog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_id", nullable = false, length = 120, unique = true)
    private String productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "unit_price", nullable = false)
    private Integer unitPrice;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Catalog catalog = (Catalog) o;
        return id != null && Objects.equals(id, catalog.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Builder
    public Catalog(Long id, String productId, String productName, Integer stock, Integer unitPrice) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.stock = stock;
        this.unitPrice = unitPrice;
    }
}