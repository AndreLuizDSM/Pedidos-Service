package com.andre.pedidosservice.order.mapper;

import com.andre.pedidosservice.order.core.OrderStatus;
import com.andre.pedidosservice.order.core.domain.OrderDomain;
import com.andre.pedidosservice.order.core.domain.OrderItemDomain;
import com.andre.pedidosservice.order.dtos.*;
import com.andre.pedidosservice.order.entities.OrderEntity;
import com.andre.pedidosservice.order.entities.OrderItemEntity;
import com.andre.pedidosservice.product.entities.ProductEntity;
import com.andre.pedidosservice.user.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class OrderMapperTest {

    OrderMapper mapper;

    LocalDateTime now;

    // entradas dos mapeamentos
    ProductEntity productEntity;
    UserEntity userEntity;
    OrderItemEntity orderItemEntity;
    OrderEntity orderEntity;
    OrderItemDomain orderItemDomain;
    OrderDomain orderDomain;
    OrderItemRequestDTO orderItemRequest;

    // resultados esperados (fixtures)
    OrderItemDomain itemDomainFromEntityFixture;
    OrderDomain orderDomainFromEntityFixture;
    OrderItemDomain itemDomainFromRequestFixture;
    OrderItemResponseDTO itemResponseFixture;
    OrderResponseDTO orderResponseFixture;
    OrderEntity orderEntityFromDomainFixture;

    @BeforeEach
    public void setup() {
        mapper = Mappers.getMapper(OrderMapper.class);

        // reaproveitamento
        now = LocalDateTime.now();

        // Será aderido ao orderItemEntity , essa propriedade não existe no orderItemDomain
        productEntity = new ProductEntity("p1", "Teclado", 150.0, 10);
        // Será aderido ao orderEntity, essa propriedade não existe no orderDomain
        userEntity = UserEntity.builder().id("u1").build();

        // Entities
        // order = null para evitar a referência bidirecional OrderEntity ↔ OrderItemEntity
        orderItemEntity = new OrderItemEntity("i1", null, productEntity, "Teclado", 150.0, 2);
        orderEntity = new OrderEntity("o1", now, now, now, 300.0, OrderStatus.PENDENTE,
                userEntity, List.of(orderItemEntity));


        // Domains
        orderItemDomain = new OrderItemDomain("p1", 2);
        orderItemDomain.setId("i1");
        orderItemDomain.setProductName("Teclado");
        orderItemDomain.setProductPrice(150.0);

        orderDomain = new OrderDomain();
        orderDomain.setId("o1");
        orderDomain.setCreatedAt(now);
        orderDomain.setUpdatedAt(now);
        orderDomain.setExpiresAt(now);
        orderDomain.setTotalAmount(300.0);
        orderDomain.setStatus(OrderStatus.PENDENTE);
        orderDomain.setUserId("u1");
        orderDomain.setOrderItems(List.of(orderItemDomain));

        // DTOS - REQUESTS E RESPONSES
        orderItemRequest = OrderItemRequestDTOFixture.build("p1", 2);

        // requestToItemDomain: só productId e quantity são mapeados; o resto fica no default
        itemDomainFromRequestFixture = new OrderItemDomain("p1", 2);

        // itemDomainToResponse: price vem de productPrice; subtotal = productPrice * quantity
        itemResponseFixture =
                OrderItemResponseDTOFixture.build("i1", "p1", "Teclado", 150.0, 2, 300.0);

        // domainToResponse: items fica null (o mapper não mapeia orderItems → items)
        orderResponseFixture = OrderResponseDTOFixture.build("o1", now, now, now,
                300.0, OrderStatus.PENDENTE, "u1", null);

        // User no OrderEntity não é mapeado pelo mapstruct , o user é injetado manualmente no método da service
        // product fica null: o mapper só tem productId (String), não consegue reconstruir a ProductEntity completo
        OrderItemEntity itemEntityFromDomain = new OrderItemEntity("i1", null, null, "Teclado", 150.0, 2);
        orderEntityFromDomainFixture = new OrderEntity("o1", now, now, now, 300.0, OrderStatus.PENDENTE,
                null, List.of(itemEntityFromDomain));
    }

    @Test
    void should_ReturnItemDomain_when_ConvertFromItemEntity(){
        OrderItemDomain domain = mapper.itemEntityToDomain(orderItemEntity);
        assertEquals(orderItemDomain, domain);
    }

    @Test
    void should_ReturnDomain_when_ConvertFromEntity(){
        OrderDomain domain = mapper.entityToDomain(orderEntity);
        assertEquals(orderDomain, domain);
    }

    @Test
    void should_ReturnItemDomain_when_ConvertFromRequest(){
        OrderItemDomain domain = mapper.requestToItemDomain(orderItemRequest);
        assertEquals(itemDomainFromRequestFixture, domain);
    }

    @Test
    void should_ReturnItemResponse_when_ConvertFromItemDomain(){
        OrderItemResponseDTO dto = mapper.itemDomainToResponse(orderItemDomain);
        assertEquals(itemResponseFixture, dto);
    }

    @Test
    void should_ReturnResponse_when_ConvertFromDomain(){
        OrderResponseDTO dto = mapper.domainToResponse(orderDomain);
        assertEquals(orderResponseFixture, dto);
    }

    @Test
    void should_ReturnEntity_when_ConvertFromDomain(){
        OrderEntity entity = mapper.domainToEntity(orderDomain);
        assertEquals(orderEntityFromDomainFixture, entity);
    }
}
