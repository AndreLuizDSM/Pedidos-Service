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

        now = LocalDateTime.now();

        productEntity = new ProductEntity("p1", "Teclado", 150.0, 10);
        userEntity = UserEntity.builder().id("u1").build();

        // order = null para evitar a referência bidirecional OrderEntity ↔ OrderItemEntity
        orderItemEntity = new OrderItemEntity("i1", null, productEntity, "Teclado", 150.0, 2);
        orderEntity = new OrderEntity("o1", now, now, now, 300.0, OrderStatus.PENDENTE,
                userEntity, List.of(orderItemEntity));

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

        orderItemRequest = OrderItemRequestDTOFixture.build("p1", 2);

        // itemEntityToDomain: productId vem de product.id; productPrice não é mapeado (fica 0.0)
        itemDomainFromEntityFixture = new OrderItemDomain("p1", 2);
        itemDomainFromEntityFixture.setId("i1");
        itemDomainFromEntityFixture.setProductName("Teclado");

        // entityToDomain: userId vem de user.id; o item mapeado não recebe productPrice (fica 0.0)
        orderDomainFromEntityFixture = new OrderDomain();
        orderDomainFromEntityFixture.setId("o1");
        orderDomainFromEntityFixture.setCreatedAt(now);
        orderDomainFromEntityFixture.setUpdatedAt(now);
        orderDomainFromEntityFixture.setExpiresAt(now);
        orderDomainFromEntityFixture.setTotalAmount(300.0);
        orderDomainFromEntityFixture.setStatus(OrderStatus.PENDENTE);
        orderDomainFromEntityFixture.setUserId("u1");
        orderDomainFromEntityFixture.setOrderItems(List.of(itemDomainFromEntityFixture));

        // requestToItemDomain: só productId e quantity são mapeados; o resto fica no default
        itemDomainFromRequestFixture = new OrderItemDomain("p1", 2);

        // itemDomainToResponse: price vem de productPrice; subtotal = productPrice * quantity
        itemResponseFixture =
                OrderItemResponseDTOFixture.build("i1", "p1", "Teclado", 150.0, 2, 300.0);

        // domainToResponse: items fica null (o mapper não mapeia orderItems → items)
        orderResponseFixture = OrderResponseDTOFixture.build("o1", now, now, now,
                300.0, OrderStatus.PENDENTE, "u1", null);

        // domainToEntity: user não é mapeado (null); no item, order/product/price não são mapeados (price 0.0)
        OrderItemEntity itemEntityFromDomain = new OrderItemEntity("i1", null, null, "Teclado", 0.0, 2);
        orderEntityFromDomainFixture = new OrderEntity("o1", now, now, now, 300.0, OrderStatus.PENDENTE,
                null, List.of(itemEntityFromDomain));
    }

    @Test
    void deveConverterItemEntityParaDomain(){
        OrderItemDomain domain = mapper.itemEntityToDomain(orderItemEntity);
        assertEquals(itemDomainFromEntityFixture, domain);
    }

    @Test
    void deveConverterEntityParaDomain(){
        OrderDomain domain = mapper.entityToDomain(orderEntity);
        assertEquals(orderDomainFromEntityFixture, domain);
    }

    @Test
    void deveConverterRequestParaItemDomain(){
        OrderItemDomain domain = mapper.requestToItemDomain(orderItemRequest);
        assertEquals(itemDomainFromRequestFixture, domain);
    }

    @Test
    void deveConverterItemDomainParaResponse(){
        OrderItemResponseDTO dto = mapper.itemDomainToResponse(orderItemDomain);
        assertEquals(itemResponseFixture, dto);
    }

    @Test
    void deveConverterDomainParaResponse(){
        OrderResponseDTO dto = mapper.domainToResponse(orderDomain);
        assertEquals(orderResponseFixture, dto);
    }

    @Test
    void deveConverterDomainParaEntity(){
        OrderEntity entity = mapper.domainToEntity(orderDomain);
        assertEquals(orderEntityFromDomainFixture, entity);
    }
}
