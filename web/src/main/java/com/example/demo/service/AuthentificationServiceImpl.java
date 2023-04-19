package com.example.demo.service;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.security.SecureRandom;
import java.util.Base64;

@Named
@ApplicationScoped
public class AuthentificationServiceImpl implements AuthentificationService {

    private final byte[] hash;
    private final Argon2Parameters.Builder builder;

    public AuthentificationServiceImpl() {
        int iterations = 2;
        int memory = 65536;
        int parallelism = 1;
        int hashLength = 32;

        builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withParallelism(parallelism)
                .withMemoryAsKB(memory)
                .withIterations(iterations);

        hash = new byte[hashLength];
    }

    public String hashPassword(String plainPassword) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        builder.withSalt(salt);
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(builder.build());
        generator.generateBytes(plainPassword.toCharArray(), hash);

        // Encodez le sel et le hachage en Base64 pour le stockage
        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        String encodedHash = Base64.getEncoder().encodeToString(hash);

        return encodedSalt + ":" + encodedHash;
    }

    public boolean verifyPassword(String plainPassword, String storedPassword) {
        String[] parts = storedPassword.split(":");
        if (parts.length != 2) {
            return false;
        }

        String encodedSalt = parts[0];

        byte[] salt = Base64.getDecoder().decode(encodedSalt);

        String newHashedPassword = encodedSalt + ":" + hashPassword(plainPassword, salt);
        // Comparez les deux hachages pour vérifier si le mot de passe est correct
        return newHashedPassword.equals(storedPassword);
    }

    private String hashPassword(String plainPassword, byte[] salt) {
        // Utilisez les mêmes paramètres que dans la méthode hashPassword
        builder.withSalt(salt);
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(builder.build());
        generator.generateBytes(plainPassword.toCharArray(), hash);

        // Encodez le hachage en Base64 pour le stockage
        return Base64.getEncoder().encodeToString(hash);
    }
}
