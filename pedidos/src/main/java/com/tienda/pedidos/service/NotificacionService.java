package com.tienda.pedidos.service;

// Abstracción de la notificación: el servicio de pedidos depende de esta interfaz
// y no de un envío por correo concreto, así puede sustituirse por un mock en pruebas
public interface NotificacionService {
    void enviarConfirmacion(String clienteEmail, double total);
}
