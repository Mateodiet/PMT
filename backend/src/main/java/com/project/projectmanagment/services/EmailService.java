package com.project.projectmanagment.services;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

    /**
     * Envoie une notification par email lors de l'assignation d'une tâche.
     * 
     * Note: Dans un environnement de production, cette méthode utiliserait
     * Spring Mail avec une configuration SMTP réelle.
     * Pour cette étude de cas, nous simulons l'envoi et loggons l'information.
     */
    public void sendTaskAssignmentNotification(String toEmail, String taskName, String projectName, String assignedBy) {
        // Simulation de l'envoi d'email
        String subject = "Nouvelle tâche assignée : " + taskName;
        String body = String.format(
            "Bonjour,\n\n" +
            "Une nouvelle tâche vous a été assignée :\n\n" +
            "Tâche : %s\n" +
            "Projet : %s\n" +
            "Assignée par : %s\n\n" +
            "Connectez-vous à PMT pour voir les détails.\n\n" +
            "Cordialement,\n" +
            "L'équipe PMT",
            taskName, projectName, assignedBy
        );

        // Log de l'email (simulation)
        log.info("========== EMAIL NOTIFICATION ==========");
        log.info("To: {}", toEmail);
        log.info("Subject: {}", subject);
        log.info("Body: {}", body);
        log.info("=========================================");

        // TODO: Pour une implémentation réelle, décommenter et configurer :
        // SimpleMailMessage message = new SimpleMailMessage();
        // message.setTo(toEmail);
        // message.setSubject(subject);
        // message.setText(body);
        // mailSender.send(message);
    }

    /**
     * Envoie une notification lors de la mise à jour d'une tâche.
     */
    public void sendTaskUpdateNotification(String toEmail, String taskName, String updateDescription) {
        String subject = "Tâche mise à jour : " + taskName;
        String body = String.format(
            "Bonjour,\n\n" +
            "Une tâche qui vous est assignée a été mise à jour :\n\n" +
            "Tâche : %s\n" +
            "Modification : %s\n\n" +
            "Connectez-vous à PMT pour voir les détails.\n\n" +
            "Cordialement,\n" +
            "L'équipe PMT",
            taskName, updateDescription
        );

        log.info("========== EMAIL NOTIFICATION ==========");
        log.info("To: {}", toEmail);
        log.info("Subject: {}", subject);
        log.info("Body: {}", body);
        log.info("=========================================");
    }

    /**
     * Envoie une invitation à rejoindre un projet.
     */
    public void sendProjectInvitation(String toEmail, String projectName, String invitedBy, String inviteLink) {
        String subject = "Invitation à rejoindre le projet : " + projectName;
        String body = String.format(
            "Bonjour,\n\n" +
            "Vous avez été invité(e) à rejoindre un projet sur PMT :\n\n" +
            "Projet : %s\n" +
            "Invité par : %s\n\n" +
            "Cliquez sur le lien suivant pour accepter l'invitation :\n%s\n\n" +
            "Cordialement,\n" +
            "L'équipe PMT",
            projectName, invitedBy, inviteLink
        );

        log.info("========== EMAIL NOTIFICATION ==========");
        log.info("To: {}", toEmail);
        log.info("Subject: {}", subject);
        log.info("Body: {}", body);
        log.info("=========================================");
    }
}