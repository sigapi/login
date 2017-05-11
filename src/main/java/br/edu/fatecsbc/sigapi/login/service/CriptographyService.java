package br.edu.fatecsbc.sigapi.login.service;

import java.io.IOException;
import java.io.StringWriter;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;

import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

@Service
public class CriptographyService {

    private static final Logger log = LoggerFactory.getLogger(CriptographyService.class);

    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;

    private KeyPair keyPair;

    @PostConstruct
    private void init() {

        KeyPairGenerator generator;
        try {
            generator = KeyPairGenerator.getInstance(ALGORITHM);
        } catch (final NoSuchAlgorithmException e) {
            log.error("Erro obtendo o KeyPairGenerator", e);
            throw new IllegalStateException(e);

        }
        generator.initialize(KEY_SIZE);

        keyPair = generator.generateKeyPair();

    }

    /**
     * Obtenção da chave privada
     *
     * @return Chave privada como {@link String}
     */
    public String getPrivateKey() {

        // Obtém a chave
        final PrivateKey key = keyPair.getPrivate();
        final byte[] encoded = key.getEncoded();
        final PrivateKeyInfo pkInfo = PrivateKeyInfo.getInstance(encoded);

        ASN1Encodable encodable;
        try {
            // Obtém o conteúdo da chave
            encodable = pkInfo.parsePrivateKey();
        } catch (final IOException e) {
            log.error("Erro realizando parser da chave privada", e);
            throw new IllegalStateException(e);
        }

        // Obtém a chave como String
        return getPemKey("RSA PRIVATE KEY", encodable.toASN1Primitive());

    }

    /**
     * Obtenção da chave pública
     *
     * @return Chave pública, como {@link String}
     */
    public String getPublicKey() {

        // Obtém a chave
        final PublicKey key = keyPair.getPublic();
        final byte[] encoded = key.getEncoded();
        final SubjectPublicKeyInfo spkInfo = SubjectPublicKeyInfo.getInstance(encoded);
        ASN1Primitive primitive;

        try {
            // Obtém o conteúdo da chave
            primitive = spkInfo.parsePublicKey();
        } catch (final IOException e) {
            log.error("Erro realizando parser da chave pública", e);
            throw new IllegalStateException(e);
        }

        // Obtém a chave como String
        return getPemKey("RSA PUBLIC KEY", primitive);

    }

    /**
     * Método genérico para obtenção de uma chave, pública ou privada
     *
     * @param type
     *            Tipo de chave
     * @param primitive
     *            Conteúdo da chave
     * @return Chave como {@link String}
     */
    private static final String getPemKey(final String type, final ASN1Primitive primitive) {

        byte[] encoded;
        try {
            encoded = primitive.getEncoded();
        } catch (final IOException e) {
            log.error("Erro obtendo conteúdo da chave primitiva", e);
            throw new IllegalStateException(e);
        }

        final PemObject po = new PemObject(type, encoded);
        final StringWriter sw = new StringWriter();
        try (PemWriter pw = new PemWriter(sw)) {
            pw.writeObject(po);
        } catch (final IOException e) {
            log.error("Erro escrevendo a chave PEM", e);
            throw new IllegalStateException(e);
        }

        return sw.toString();

    }

    /**
     * Executa a operação no {@link Cipher}
     *
     * @param mode
     *            Modo de operação, podendo ser {@link Cipher#ENCRYPT_MODE} ou {@link Cipher#DECRYPT_MODE}
     * @param key
     *            Chave, pública ou privada, que será utilizada
     * @param content
     *            Conteúdo a ser utilizado na operação
     * @return Resultado da operação
     */
    private byte[] executeCipher(final int mode, final Key key, final byte[] content) {

        Cipher cipher = null;

        try {
            // Inicializa
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(mode, key);
        } catch (final Exception e) {
            log.error("Erro inicializando objetos de criptografia", e);
        }

        if (cipher != null) {
            try {
                // Realiza a operação
                return cipher.doFinal(content);
            } catch (final Exception e) {
                log.error("Erro realizando operação de (des)criptografia - %d:", mode, e);
            }
        }

        return new byte[] {};

    }

    /**
     * Realiza a criptografia de determinado texto
     *
     * @param text
     *            Texto a ser criptografado
     * @return Texto criptografado, codificado em Base64
     */
    public String encrypt(final String text) {

        // Obtém o texto como byte[]
        final byte[] content = text.getBytes();

        // Realiza a criptografia, utilizando a chave pública
        final byte[] result = executeCipher(Cipher.ENCRYPT_MODE, keyPair.getPublic(), content);

        // Codifica em Base64 e retorna
        return Base64Utils.encodeToString(result);
    }

    /**
     * Realiza a descriptografia de determinado texto.
     *
     * @param text
     *            Texto a ser descriptografado, codificado em Base64
     * @return Texto descriptografado
     */
    public String decrypt(final String text) {

        // Decofidica o texto original, que deve estar em Base64
        final byte[] content = Base64Utils.decodeFromString(text);

        // Realiza a descriptografia, utilizando a chave privada
        final byte[] result = executeCipher(Cipher.DECRYPT_MODE, keyPair.getPrivate(), content);

        // Retorna como String
        return new String(result);
    }

}
