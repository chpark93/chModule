package com.ch.catalog.application;

import com.ch.catalog.dto.response.CatalogResponse;

import java.util.List;

public interface CatalogService {

    List<CatalogResponse> getAllCatalogs();
}
