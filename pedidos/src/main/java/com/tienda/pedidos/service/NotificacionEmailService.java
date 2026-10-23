package com.tienda.pedidos.service;

// Código de partida: clase concreta sin interfaz, instanciada con "new" desde el controller
public class NotificacionEmailService {

    public void enviar(String destinatario, String mensaje) {
        System.out.println("Enviando correo a " + destinatario + ": " + mensaje);
    }
}
