package com.andre.pedidosservice.order.gateways.in;

/*
 * Porta de entrada para a feature de expiração de pedidos.
 * Contrato usado pelo scheduler para acionar a lógica de domínio.
 */
public interface OrderExpirationGatewayService {

    void expireOrders();

    void deleteExpiredOrders();
}
