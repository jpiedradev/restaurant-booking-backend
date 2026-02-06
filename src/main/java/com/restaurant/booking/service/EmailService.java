package com.restaurant.booking.service;

import com.restaurant.booking.model.Reservation;
import com.restaurant.booking.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from-name}")
    private String fromName;

    /**
     * Envía un email simple (texto plano)
     */
    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            System.out.println("Email enviado a: " + to);
        } catch (Exception e) {
            System.err.println("Error al enviar email: " + e.getMessage());
        }
    }

    /**
     * Envía un email con formato HTML
     */
    @Async
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Email HTML enviado a: " + to);
        } catch (MessagingException e) {
            System.err.println("Error al enviar email HTML: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado al enviar email: " + e.getMessage());
        }
    }

    /**
     * Email de bienvenida al registrarse
     */
    @Async
    public void sendWelcomeEmail(User user) {
        String subject = "¡Bienvenido a Restaurant Booking!";

        String htmlContent = buildWelcomeEmailHtml(user);

        sendHtmlEmail(user.getEmail(), subject, htmlContent);
    }

    /**
     * Email de confirmación de reserva creada
     */
    @Async
    public void sendReservationCreatedEmail(User user, Reservation reservation, String tableNumber) {
        String subject = "Reserva creada - Confirmación pendiente";

        String htmlContent = buildReservationCreatedEmailHtml(user, reservation, tableNumber);

        sendHtmlEmail(user.getEmail(), subject, htmlContent);
    }

    /**
     * Email de reserva confirmada
     */
    @Async
    public void sendReservationConfirmedEmail(User user, Reservation reservation, String tableNumber) {
        String subject = "¡Tu reserva ha sido confirmada!";

        String htmlContent = buildReservationConfirmedEmailHtml(user, reservation, tableNumber);

        sendHtmlEmail(user.getEmail(), subject, htmlContent);
    }

    /**
     * Email de reserva cancelada
     */
    @Async
    public void sendReservationCancelledEmail(User user, Reservation reservation, String tableNumber) {
        String subject = "Reserva cancelada";

        String htmlContent = buildReservationCancelledEmailHtml(user, reservation, tableNumber);

        sendHtmlEmail(user.getEmail(), subject, htmlContent);
    }

    /**
     * Email recordatorio 24h antes de la reserva
     */
    @Async
    public void sendReservationReminderEmail(User user, Reservation reservation, String tableNumber) {
        String subject = "Recordatorio: Tu reserva es mañana";

        String htmlContent = buildReservationReminderEmailHtml(user, reservation, tableNumber);

        sendHtmlEmail(user.getEmail(), subject, htmlContent);
    }

    // ========== MÉTODOS PRIVADOS PARA CONSTRUIR HTML ==========

    private String buildWelcomeEmailHtml(User user) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #2563eb; color: white; padding: 20px; text-align: center; }
                        .content { background-color: #f9fafb; padding: 30px; }
                        .footer { background-color: #e5e7eb; padding: 15px; text-align: center; font-size: 12px; }
                        .button { background-color: #2563eb; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; display: inline-block; margin-top: 15px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>¡Bienvenido a Restaurant Booking!</h1>
                        </div>
                        <div class="content">
                            <h2>Hola %s,</h2>
                            <p>Gracias por registrarte en nuestro sistema de reservas.</p>
                            <p>Ahora puedes reservar tu mesa favorita de forma rápida y sencilla.</p>
                            <p><strong>Tu cuenta:</strong></p>
                            <ul>
                                <li>Usuario: %s</li>
                                <li>Email: %s</li>
                            </ul>
                            <p>¿Listo para hacer tu primera reserva?</p>
                            <a href="http://localhost:5173/new-reservation" class="button">Reservar ahora</a>
                        </div>
                        <div class="footer">
                            <p>Restaurant Booking System &copy; 2026</p>
                            <p>Este email fue enviado automáticamente, por favor no responder.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(user.getFullName(), user.getUsername(), user.getEmail());
    }

    private String buildReservationCreatedEmailHtml(User user, Reservation reservation, String tableNumber) {
        String formattedDate = formatDate(reservation.getReservationDate());
        String formattedTime = formatTime(reservation.getReservationTime());

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #f59e0b; color: white; padding: 20px; text-align: center; }
                        .content { background-color: #fffbeb; padding: 30px; }
                        .reservation-details { background-color: white; padding: 20px; border-left: 4px solid #f59e0b; margin: 20px 0; }
                        .footer { background-color: #e5e7eb; padding: 15px; text-align: center; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Reserva Creada</h1>
                        </div>
                        <div class="content">
                            <h2>Hola %s,</h2>
                            <p>Hemos recibido tu solicitud de reserva.</p>
                            <div class="reservation-details">
                                <h3>Detalles de tu reserva:</h3>
                                <p><strong>📅 Fecha:</strong> %s</p>
                                <p><strong>🕐 Hora:</strong> %s</p>
                                <p><strong>👥 Comensales:</strong> %d personas</p>
                                <p><strong>🪑 Mesa:</strong> Mesa %s</p>
                                <p><strong>Estado:</strong> <span style="color: #f59e0b;">Pendiente de confirmación</span></p>
                            </div>
                            <p>Nuestro equipo revisará tu solicitud y te confirmaremos pronto.</p>
                            <p>Recibirás un email cuando tu reserva sea confirmada.</p>
                        </div>
                        <div class="footer">
                            <p>Restaurant Booking System &copy; 2026</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                user.getFullName(),
                formattedDate,
                formattedTime,
                reservation.getGuests(),
                tableNumber
        );
    }

    private String buildReservationConfirmedEmailHtml(User user, Reservation reservation, String tableNumber) {
        String formattedDate = formatDate(reservation.getReservationDate());
        String formattedTime = formatTime(reservation.getReservationTime());

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #10b981; color: white; padding: 20px; text-align: center; }
                        .content { background-color: #f0fdf4; padding: 30px; }
                        .reservation-details { background-color: white; padding: 20px; border-left: 4px solid #10b981; margin: 20px 0; }
                        .footer { background-color: #e5e7eb; padding: 15px; text-align: center; font-size: 12px; }
                        .success { color: #10b981; font-weight: bold; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>✓ ¡Reserva Confirmada!</h1>
                        </div>
                        <div class="content">
                            <h2>Hola %s,</h2>
                            <p class="success">¡Excelentes noticias! Tu reserva ha sido confirmada.</p>
                            <div class="reservation-details">
                                <h3>Detalles de tu reserva:</h3>
                                <p><strong>📅 Fecha:</strong> %s</p>
                                <p><strong>🕐 Hora:</strong> %s</p>
                                <p><strong>👥 Comensales:</strong> %d personas</p>
                                <p><strong>🪑 Mesa:</strong> Mesa %s</p>
                                <p><strong>Estado:</strong> <span style="color: #10b981;">Confirmada</span></p>
                            </div>
                            <p><strong>¡Te esperamos!</strong></p>
                            <p>Por favor, llega 10 minutos antes de tu hora reservada.</p>
                            <p>Si necesitas cancelar o modificar tu reserva, por favor contáctanos con anticipación.</p>
                        </div>
                        <div class="footer">
                            <p>Restaurant Booking System &copy; 2026</p>
                            <p>Teléfono: +51 987 654 321</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                user.getFullName(),
                formattedDate,
                formattedTime,
                reservation.getGuests(),
                tableNumber
        );
    }

    private String buildReservationCancelledEmailHtml(User user, Reservation reservation, String tableNumber) {
        String formattedDate = formatDate(reservation.getReservationDate());
        String formattedTime = formatTime(reservation.getReservationTime());

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #ef4444; color: white; padding: 20px; text-align: center; }
                        .content { background-color: #fef2f2; padding: 30px; }
                        .reservation-details { background-color: white; padding: 20px; border-left: 4px solid #ef4444; margin: 20px 0; }
                        .footer { background-color: #e5e7eb; padding: 15px; text-align: center; font-size: 12px; }
                        .button { background-color: #2563eb; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; display: inline-block; margin-top: 15px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Reserva Cancelada</h1>
                        </div>
                        <div class="content">
                            <h2>Hola %s,</h2>
                            <p>Tu reserva ha sido cancelada.</p>
                            <div class="reservation-details">
                                <h3>Detalles de la reserva cancelada:</h3>
                                <p><strong>📅 Fecha:</strong> %s</p>
                                <p><strong>🕐 Hora:</strong> %s</p>
                                <p><strong>👥 Comensales:</strong> %d personas</p>
                                <p><strong>🪑 Mesa:</strong> Mesa %s</p>
                                <p><strong>Estado:</strong> <span style="color: #ef4444;">Cancelada</span></p>
                            </div>
                            <p>Esperamos verte pronto en nuestro restaurante.</p>
                            <a href="http://localhost:5173/new-reservation" class="button">Hacer nueva reserva</a>
                        </div>
                        <div class="footer">
                            <p>Restaurant Booking System &copy; 2026</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                user.getFullName(),
                formattedDate,
                formattedTime,
                reservation.getGuests(),
                tableNumber
        );
    }

    private String buildReservationReminderEmailHtml(User user, Reservation reservation, String tableNumber) {
        String formattedDate = formatDate(reservation.getReservationDate());
        String formattedTime = formatTime(reservation.getReservationTime());

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #8b5cf6; color: white; padding: 20px; text-align: center; }
                        .content { background-color: #faf5ff; padding: 30px; }
                        .reservation-details { background-color: white; padding: 20px; border-left: 4px solid #8b5cf6; margin: 20px 0; }
                        .footer { background-color: #e5e7eb; padding: 15px; text-align: center; font-size: 12px; }
                        .reminder { background-color: #fef3c7; padding: 15px; border-radius: 5px; margin: 20px 0; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🔔 Recordatorio de Reserva</h1>
                        </div>
                        <div class="content">
                            <h2>Hola %s,</h2>
                            <div class="reminder">
                                <p><strong>⏰ ¡Tu reserva es mañana!</strong></p>
                            </div>
                            <div class="reservation-details">
                                <h3>Detalles de tu reserva:</h3>
                                <p><strong>📅 Fecha:</strong> %s</p>
                                <p><strong>🕐 Hora:</strong> %s</p>
                                <p><strong>👥 Comensales:</strong> %d personas</p>
                                <p><strong>🪑 Mesa:</strong> Mesa %s</p>
                            </div>
                            <p><strong>Recomendaciones:</strong></p>
                            <ul>
                                <li>Llega 10 minutos antes de tu hora reservada</li>
                                <li>Si necesitas cancelar, hazlo con al menos 2 horas de anticipación</li>
                                <li>Trae tu confirmación de reserva</li>
                            </ul>
                            <p>¡Nos vemos mañana!</p>
                        </div>
                        <div class="footer">
                            <p>Restaurant Booking System &copy; 2026</p>
                            <p>Teléfono: +51 987 654 321</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                user.getFullName(),
                formattedDate,
                formattedTime,
                reservation.getGuests(),
                tableNumber
        );
    }

    // ========== MÉTODOS AUXILIARES ==========

    private String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy");
        return date.format(formatter);
    }

    private String formatTime(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }
}