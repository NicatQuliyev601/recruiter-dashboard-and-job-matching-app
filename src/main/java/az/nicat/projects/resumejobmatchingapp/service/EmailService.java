package az.nicat.projects.resumejobmatchingapp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    public void sendEmailRegister(String to, String subject, String confirmationLink) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);

            String content = "<div style='font-family: Arial, sans-serif; line-height: 1.5;'>" +
                    "<h2 style='color: #333;'>Welcome to Resume Matcher!</h2>" +
                    "<p>Thank you for registering. Please confirm your email address by clicking the link below:</p>" +
                    "<p style='margin: 20px 0;'><a href='" + confirmationLink + "' " +
                    "style='padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px;'>Confirm Email</a></p>" +
                    "<p>If you didn't register, you can safely ignore this email.</p>" +
                    "<br><p>— Resume Matcher Team</p>" +
                    "</div>";

            helper.setText(content, true); // true = HTML

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send confirmation email", e);
        }
    }

    public void sendEmailScheduleInterview(String to, String subject, String candidateName, String jobTitle, String interviewDateTime) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);

            String content = "<div style='font-family: Arial, sans-serif; line-height: 1.6;'>" +
                    "<h2 style='color: #2E86C1;'>Müsahibə Dəvəti - Resume Matcher</h2>" +
                    "<p>Hörmətli " + candidateName + ",</p>" +
                    "<p>Əməkdaşlıq üçün müraciət etdiyiniz <strong>" + jobTitle + "</strong> vakansiyası üzrə təqdim etdiyiniz profil dəyərləndirilmiş və ilkin seçim mərhələsindən keçmişdir.</p>" +
                    "<p>Sizi aşağıda qeyd olunan tarixdə müsahibədə iştirak etməyə dəvət edirik:</p>" +
                    "<p><strong>Müsahibə tarixi və vaxtı:</strong> " + interviewDateTime + "</p>" +
                    "<p><strong>Müsahibə formatı:</strong> Ətraflı məlumat (onlayn link, yer və s.) əlavə olaraq təqdim olunacaq və ya əlaqə saxlanılacaqdır.</p>" +
                    "<p>Zəhmət olmasa, göstərilən vaxtda hazır olun və iştirakınızı əvvəlcədən təsdiqləyin.</p>" +
                    "<p>Əgər hər hansı sualınız yaranarsa və ya müsahibə vaxtını dəyişmək ehtiyacınız olarsa, bizimlə əlaqə saxlamaqdan çəkinməyin.</p>" +
                    "<br><p>İştirakınıza görə təşəkkür edir, uğurlar arzulayırıq!</p>" +
                    "<p>Hörmətlə,<br><strong>Resume Matcher Komandası</strong></p>" +
                    "</div>";

            helper.setText(content, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Müsahibə dəvəti emaili göndərilə bilmədi", e);
        }
    }



}