package org.hanghae99.fcfs.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VigenereCipher {
    @Value("${vigenere.cipher.key}")
    private static String key;

    //	Encryption
    //	Encryption Logic: Using ASCII Dec Representation:
    //	Example:
    //	ASCII: "H" is 72 && "S" is 83
    //	((72-65) + (83-65)) % 26 + 65 >> Encrypted "Z"
    public static String encrypt(String plainText) {
        StringBuilder sb = new StringBuilder();
        plainText = plainText.toUpperCase();
        int bIdx = 0;
        for (int i = 0; i < plainText.length(); i++) {
            char a = plainText.charAt(i);

            if (plainText.charAt(i) == ' ') sb.append(" ");
            else sb.append((char) (((a - 65) + (key.charAt(bIdx) - 65)) % 26 + 65));

            bIdx++;
            bIdx %= key.length();
        }
        return sb.toString();
    }

    //	Decryption
    //	Decryption Logic: Using ASCII Dec Representation:
    //	Example:
    //	ASCII: "Z" is 90 && "S" is 83
    //	(90-83+26) % 26 + 65 >> Encrypted "Z"
    public static String decrypt(String cipherText) {
        StringBuilder sb = new StringBuilder();
        cipherText = cipherText.toUpperCase();
        int bIdx = 0;
        for (int i = 0; i < cipherText.length(); i++) {
            char a = cipherText.charAt(i);

            if (cipherText.charAt(i) == ' ') sb.append(" ");
            else sb.append((char) ((a - key.charAt(bIdx) + 26) % 26 + 65));

            bIdx++;
            bIdx %= key.length();
        }
        return sb.toString();
    }
}
