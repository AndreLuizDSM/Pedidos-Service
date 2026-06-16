package com.andre.pedidosservice.product.adapters.in;

import com.andre.pedidosservice.product.core.domain.ProductDomain;
import com.andre.pedidosservice.product.dtos.ProductMapper;
import com.andre.pedidosservice.product.dtos.ProductRequestDTO;
import com.andre.pedidosservice.product.dtos.ProductResponseDTO;
import com.andre.pedidosservice.product.gateways.in.ProductServiceGateway;
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
public class ProductController {

    private final ProductServiceGateway service;
    private final ProductMapper mapper;

    // Somente ADMIN pode criar produtos (regra definida no SecurityConfig)
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO dto) {
        ProductDomain domain = mapper.requestToDomain(dto);
        return ResponseEntity.ok(mapper.domainToResponse(service.createProduct(domain)));
    }

    // Qualquer usuário autenticado pode listar produtos para fazer pedidos
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> response = service.getAllProducts()
                .stream()
                .map(mapper::domainToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    // Qualquer usuário autenticado pode consultar um produto específico
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(mapper.domainToResponse(service.getProductById(id)));
    }

    // Somente ADMIN pode alterar preço, nome ou estoque de um produto
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable String id,
                                                            @Valid @RequestBody ProductRequestDTO dto) {
        ProductDomain domain = mapper.requestToDomain(dto);
        return ResponseEntity.ok(mapper.domainToResponse(service.updateProduct(id, domain)));
    }

    // Somente ADMIN pode remover um produto do catálogo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        service.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
}
