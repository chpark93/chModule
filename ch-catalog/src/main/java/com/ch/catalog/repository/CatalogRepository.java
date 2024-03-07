package com.ch.catalog.repository;

import com.ch.catalog.domain.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogRepository extends JpaRepository<Catalog, Long> {

    Catalog findByProductId(String productId);

}
