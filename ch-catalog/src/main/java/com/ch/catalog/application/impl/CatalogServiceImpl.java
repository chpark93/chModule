package com.ch.catalog.application.impl;

import com.ch.catalog.application.CatalogService;
import com.ch.catalog.domain.Catalog;
import com.ch.catalog.dto.response.CatalogResponse;
import com.ch.catalog.repository.CatalogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {

    private final CatalogRepository catalogRepository;

    @Override
    public List<CatalogResponse> getAllCatalogs() {
        Iterable<Catalog> findAllCatalog = catalogRepository.findAll();

        List<CatalogResponse> catalogs = new ArrayList<>();
        findAllCatalog.forEach(info ->
                catalogs.add(new ModelMapper().map(info, CatalogResponse.class))
        );

        return catalogs;
    }
}
