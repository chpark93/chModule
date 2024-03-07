package com.ch.catalog.api;

import com.ch.catalog.application.CatalogService;
import com.ch.catalog.dto.response.CatalogResponse;
import com.ch.core.common.response.ErrorResponse;
import com.ch.core.common.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CatalogApi {

    private final CatalogService catalogService;
    private final Response response;

    @Operation(summary = "getProfileFromUserId", description = "User getProfileFromUserId API.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CatalogResponse.class))))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/catalogs")
    public ResponseEntity<Response.Body> getCatalogs() {

        return response.success(catalogService.getAllCatalogs(), "Success", HttpStatus.OK);
    }

}
