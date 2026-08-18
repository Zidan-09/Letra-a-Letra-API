package com.letraaletra.api.features.admin.infrastructure.service;

import com.letraaletra.api.features.admin.application.port.PasswordResetTokenEmailService;
import com.letraaletra.api.shared.application.port.EmailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenEmail implements PasswordResetTokenEmailService {
    private final EmailSenderService mailSender;

    @Override
    public void send(String email, String recipient, String token) {

        String resetUrl = "https://admin.letraaletradev.qzz.io/redefinir-senha?token=" + token;

        mailSender.send(email, "Redefinição de Senha - Painel Administrativo", """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <title>Redefinição de Senha</title>
            </head>
            <body style="margin: 0; padding: 0; background-color: #0b0e14; background-image: radial-gradient(at 50%% 0%%, hsla(225,39%%,30%%,0.2) 0, transparent 70%%); font-family: 'Inter', system-ui, -apple-system, sans-serif; -webkit-font-smoothing: antialiased; color: #cbd5e1;">

              <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" border="0" style="background-color: #0b0e14; padding: 40px 10px;">
                <tr>
                  <td align="center">

                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" border="0" style="max-width: 580px; background-color: #131722; border: 1px solid #1e293b; border-radius: 12px; overflow: hidden; box-shadow: 0 10px 30px rgba(0,0,0,.5);">

                      <tr>
                        <td style="padding: 36px 36px 20px 36px; text-align: left; border-bottom: 1px solid #1e293b;">
                          <h1 style="color:#ffffff; font-size:24px; font-weight:700; margin:0 0 4px 0; letter-spacing:-0.5px;">
                            LETRA A LETRA
                          </h1>

                          <p style="color:#64748b; margin:0; font-size:14px;">
                            Painel Administrativo
                          </p>
                        </td>
                      </tr>

                      <tr>
                        <td style="padding:36px; color:#cbd5e1; font-size:15px; line-height:1.6;">

                          <h2 style="color:#f8fafc; margin-top:0; font-size:20px; font-weight:600;">
                            Redefinição de senha
                          </h2>

                          <p style="margin-top:0; color:#94a3b8;">
                            Olá <strong>%s</strong>!
                          </p>

                          <p style="color:#94a3b8;">
                            Recebemos uma solicitação para redefinir a senha da sua conta de
                            <strong style="color:#f8fafc;">Administrador</strong>.
                            Caso tenha sido você, clique no botão abaixo para criar uma nova senha.
                          </p>

                          <div style="background-color:#181d2a; border:1px solid #283044; border-radius:8px; padding:16px; margin:24px 0;">
                            <p style="margin:0; font-size:13px; color:#a5b4fc;">
                              🔒 <strong>Importante:</strong> Este link é válido por
                              <strong>15 minutos</strong> e poderá ser utilizado apenas
                              <strong>uma única vez</strong>.
                            </p>
                          </div>

                          <table role="presentation" cellspacing="0" cellpadding="0" border="0" style="margin:32px 0;">
                            <tr>
                              <td align="center" style="border-radius:8px; background:#dc2626; background-image:linear-gradient(135deg,#dc2626 0%%,#b91c1c 100%%);">
                                <a href="%s" target="_blank"
                                   style="padding:14px 28px; border-radius:8px; color:#ffffff; font-size:15px; font-weight:600; text-decoration:none; display:inline-block; box-shadow:0 4px 12px rgba(185,28,28,.35);">
                                  Redefinir Senha
                                </a>
                              </td>
                            </tr>
                          </table>

                          <hr style="border:none; border-top:1px solid #1e293b; margin:32px 0 24px 0;">

                          <p style="font-size:13px; color:#64748b; margin-bottom:8px;">
                            Se o botão não funcionar, copie e cole o link abaixo no navegador:
                          </p>

                          <p style="font-size:12px; font-family:'JetBrains Mono','Fira Code',Consolas,monospace; word-break:break-all; margin:0; background-color:#0b0e14; padding:10px; border-radius:6px; border:1px solid #1e293b;">
                            <a href="%s" style="color:#818cf8; text-decoration:none;">%s</a>
                          </p>

                        </td>
                      </tr>

                      <tr>
                        <td style="background-color:#0d1017; padding:24px 36px; text-align:left; color:#64748b; font-size:12px; border-top:1px solid #1e293b;">

                          <p style="margin:0 0 6px 0;">
                            Caso você não tenha solicitado a redefinição da senha, ignore este e-mail. Nenhuma alteração será realizada em sua conta.
                          </p>

                          <p style="margin:0; color:#475569;">
                            &copy; 2026 Letra a Letra. Todos os direitos reservados.
                          </p>

                        </td>
                      </tr>

                    </table>

                  </td>
                </tr>
              </table>

            </body>
            </html>
            """.formatted(
                recipient,
                resetUrl,
                resetUrl,
                resetUrl
        ));
    }
}