package com.frame24.api.common.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Serviço para envio de emails.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String emailFrom;

    @Value("${app.email.from-name}")
    private String emailFromName;

    /**
     * Envia email HTML de forma assíncrona.
     *
     * @param to          Email do destinatário
     * @param subject     Assunto
     * @param htmlContent Conteúdo HTML
     */
    @Async
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailFrom, emailFromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email enviado para: {}", to);
        } catch (Exception e) {
            log.error("Erro ao enviar email para {}: {}", to, e.getMessage());
        }
    }

    /**
     * Gera HTML para email de verificação.
     */
    public String buildVerificationEmail(String userName, String verificationLink) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: #4F46E5; color: white; padding: 20px; text-align: center; }
                        .content { padding: 30px; background: #f9fafb; }
                        .button { display: inline-block; padding: 12px 24px; background: #4F46E5;
                                  color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                        .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Frame 24 ERP</h1>
                        </div>
                        <div class="content">
                            <h2>Olá, %s!</h2>
                            <p>Obrigado por se registrar no Frame 24 ERP.</p>
                            <p>Para ativar sua conta, clique no botão abaixo:</p>
                            <a href="%s" class="button">Verificar Email</a>
                            <p>Ou copie e cole este link no navegador:</p>
                            <p style="word-break: break-all; color: #4F46E5;">%s</p>
                            <p>Este link expira em 24 horas.</p>
                        </div>
                        <div class="footer">
                            <p>Se você não criou esta conta, ignore este email.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(userName, verificationLink, verificationLink);
    }

    /**
     * Gera HTML para email de reset de senha.
     */
    public String buildPasswordResetEmail(String userName, String resetLink) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: #DC2626; color: white; padding: 20px; text-align: center; }
                        .content { padding: 30px; background: #f9fafb; }
                        .button { display: inline-block; padding: 12px 24px; background: #DC2626;
                                  color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                        .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Redefinição de Senha</h1>
                        </div>
                        <div class="content">
                            <h2>Olá, %s!</h2>
                            <p>Recebemos uma solicitação para redefinir sua senha.</p>
                            <p>Clique no botão abaixo para criar uma nova senha:</p>
                            <a href="%s" class="button">Redefinir Senha</a>
                            <p>Ou copie e cole este link no navegador:</p>
                            <p style="word-break: break-all; color: #DC2626;">%s</p>
                            <p>Este link expira em 1 hora.</p>
                            <p><strong>Se você não solicitou esta redefinição, ignore este email.</strong></p>
                        </div>
                        <div class="footer">
                            <p>Por segurança, nunca compartilhe este link com ninguém.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(userName, resetLink, resetLink);
    }
}
