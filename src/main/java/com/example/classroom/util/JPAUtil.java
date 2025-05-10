package com.example.classroom.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;

public class JPAUtil {
    private static EntityManagerFactory emf;

    static {
        try {
            System.out.println("Initialisation de JPA...");
            emf = Persistence.createEntityManagerFactory("classroomPU");
            System.out.println("EntityManagerFactory créé avec succès!");
        } catch (PersistenceException e) {
            System.err.println("Erreur lors de l'initialisation de JPA : " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        } catch (Exception e) {
            System.err.println("Erreur inattendue : " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            throw new IllegalStateException("EntityManagerFactory n'est pas initialisé");
        }
        return emf;
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
