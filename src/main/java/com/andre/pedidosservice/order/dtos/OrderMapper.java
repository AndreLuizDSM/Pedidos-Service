package com.andre.pedidosservice.order.dtos;

import com.andre.pedidosservice.order.core.domain.OrderDomain;
import com.andre.pedidosservice.order.core.domain.OrderItemDomain;
import com.andre.pedidosservice.order.entities.OrderEntity;
import com.andre.pedidosservice.order.entities.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // ─── OrderItemEntity → OrderItemDomain ────────────────────────────────────

    /*
      product.id: navega no relacionamento @ManyToOne para extrair apenas o ID do produto.
      O campo "order" de OrderItemEntity não tem destino em OrderItemDomain, então é ignorado
      automaticamente — evita loops na referência bidirecional OrderEntity ↔ OrderItemEntity.
     */
    @Mapping(source = "product.id", target = "productId")
    OrderItemDomain itemEntityToDomain(OrderItemEntity entity);

    // ─── OrderEntity → OrderDomain ────────────────────────────────────────────

    /*
      user.id → userId: extrai apenas o ID do UserEntity sem carregar o objeto completo.
     */

    @Mapping(source = "user.id", target = "userId")
    OrderDomain entityToDomain(OrderEntity entity);

    // ─── OrderItemRequestDTO → OrderItemDomain ────────────────────────────────

    // Converte o DTO de request (productId + quantity) para domain antes de chamar o serviço
    OrderItemDomain requestToItemDomain(OrderItemRequestDTO dto);

    // ─── OrderItemDomain → OrderItemResponseDTO ───────────────────────────────

    @Mapping(source = "productPrice", target = "price")
    @Mapping(target = "subtotal", expression = "java(domain.getProductPrice() * domain.getQuantity())")
    OrderItemResponseDTO itemDomainToResponse(OrderItemDomain domain);

    OrderResponseDTO domainToResponse(OrderDomain domain);

    OrderEntity domainToEntity(OrderDomain domain);
}
