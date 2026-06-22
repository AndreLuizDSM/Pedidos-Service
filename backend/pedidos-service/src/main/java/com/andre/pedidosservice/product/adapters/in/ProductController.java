package com.andre.pedidosservice.product.adapters.in;

import com.andre.pedidosservice.product.core.domain.ProductDomain;
import com.andre.pedidosservice.product.dtos.ProductMapper;
import com.andre.pedidosservice.product.dtos.ProductRequestDTO;
import com.andre.pedidosservice.product.dtos.ProductResponseDTO;
import com.andre.pedidosservice.product.gateways.in.ProductServiceGateway;
import com.andre.pedidosservice.security.SecurityConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/*
 * Adapter de entrada: recebe as requisições HTTP e delega ao serviço de produtos.
 * As regras de autorização (ADMIN/CLIENT) são aplicadas pelo SecurityConfig,
 * não pelo controller — separação de responsabilidades.
 */
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Tag(name = "Products")
@SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME)
public class ProductController {

    private final ProductServiceGateway service;
    private final ProductMapper mapper;

    // Somente ADMIN pode criar produtos (regra definida no SecurityConfig)
    @PostMapping
    @Operation(summary = "Create product", description = "Register a new product in the catalog (ADMIN only)")
    @ApiResponse(responseCode = "200", description = "Product created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden: requires ADMIN role")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO dto) {
        ProductDomain domain = mapper.requestToDomain(dto);
        return ResponseEntity.ok(mapper.domainToResponse(service.createProduct(domain)));
    }

    // Qualquer usuário autenticado pode listar produtos para fazer pedidos
    @GetMapping
    @Operation(summary = "List products", description = "Retrieve all products in the catalog")
    @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> response = service.getAllProducts()
                .stream()
                .map(mapper::domainToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    // Qualquer usuário autenticado pode consultar um produto específico
    @GetMapping("/{id}")
    @Operation(summary = "Get product by id", description = "Retrieve a product by its identifier")
    @ApiResponse(responseCode = "200", description = "Product found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(mapper.domainToResponse(service.getProductById(id)));
    }

    // Somente ADMIN pode alterar preço, nome ou estoque de um produto
    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update name, price or stock of a product (ADMIN only)")
    @ApiResponse(responseCode = "200", description = "Product updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden: requires ADMIN role")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable String id,
                                                            @Valid @RequestBody ProductRequestDTO dto) {
        ProductDomain domain = mapper.requestToDomain(dto);
        return ResponseEntity.ok(mapper.domainToResponse(service.updateProduct(id, domain)));
    }

    // Somente ADMIN pode remover um produto do catálogo
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Remove a product from the catalog (ADMIN only)")
    @ApiResponse(responseCode = "200", description = "Product deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden: requires ADMIN role")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        service.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
}
