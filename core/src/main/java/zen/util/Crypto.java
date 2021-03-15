package zen.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.concurrent.Callable;

public class Crypto {
    Crypto() {throw new IllegalStateException("Crypto cannot be initialized");}

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    static final String FLAG = "{zenc}";

    static <R> R _try(Callable<R> action, String message) {
        try {
            return action.call();
        }
        catch(Exception e) {
            throw new RuntimeException(message, e);
        }
    }

    private static Module local;
    public static void set(Module crypto) {local = crypto;}
    public static void destroy() {local = null;}

    public static String encode(String value) { return encode(FLAG, value);}
    public static String encode(String flag, String value)
    {
        if (value != null && !value.startsWith(flag)) {
            final String base = value;
            value = _try(() -> {
                byte[] cksum = new byte[16];
                SecureRandom.getInstanceStrong().nextBytes(cksum);
                String pass = new String(cksum, CHARSET);

                Module cm = new GCMModule(flag).build(pass, cksum);
                byte[] enc = cm.encrypt(base.getBytes(CHARSET));

                ByteBuffer buff = ByteBuffer.allocate(enc.length + cksum.length);
                buff.put(cksum);
                buff.put(enc);
                return cm.encode(buff.array());
            }, "Encoding failed");
        }
        return value;
    }

    public static String decode(String value) {return decode(FLAG, value);}
    public static String decode(String flag, String value)
    {
        if (value != null && value.startsWith(flag))
        {
            return _try(() -> {
                Module cm = new GCMModule(flag);
                byte[] base = cm.decode(value);
                byte[] cksum = new byte[16];
                ByteBuffer buff = ByteBuffer.wrap(base);

                buff.get(cksum);
                byte[] secret = new byte[buff.remaining()];
                buff.get(secret);

                String pass = new String(cksum, CHARSET);
                cm.build(pass, cksum);
                return new String(cm.decrypt(secret), CHARSET);
            }, "Decoding failed");
        }
        return value;
    }

    public static String encrypt(String value)
    {
        if(local != null && local.canEncode(value))
        {
            byte[] enc = local.encrypt(value.getBytes(CHARSET));
            value = local.encode(enc);
        }
        return value;
    }

    public static String decrypt(String value)
    {
        if (local != null && local.canDecode(value))
        {
            byte[] enc = local.decode(value);
            value = new String(local.decrypt(enc), CHARSET);
        }
        return value;
    }

    public static class Credential
    {
        private final String password;
        private final String iv;

        public Credential(String flag, String password, String iv) {
            this.password = decode(flag, password);
            this.iv = decode(flag, iv);
        }
        public Credential(String password, String iv) {
            this(FLAG, password, iv);
        }

        public String password() {return password;}
        public byte[] iv() { return iv.getBytes(CHARSET);}
    }

    public interface Module
    {
        boolean canEncode(String value);
        boolean canDecode(String value);

        Module build(String pass, byte[] iv);
        byte[] encrypt(byte[] val);
        byte[] decrypt(byte[] val);

        String encode(byte[] val);
        byte[] decode(String val);
    }

    public static class GCMModule implements Module
    {
        private static final String CIPHER_ALG = "AES/GCM/NoPadding";
        private final String flag;

        private AlgorithmParameterSpec iv;
        private Key key;
        private boolean built;

        public GCMModule(String idFlag) {
            this.flag = idFlag;
            if(flag == null)
                throw new IllegalStateException("Crypto module must have identifier");
            built = false;
        }

        public boolean canEncode(String value) {
            return value != null && !value.startsWith(flag);
        }

        public boolean canDecode(String value) {
            return value != null && value.startsWith(flag);
        }

        public Module build(String pass, byte[] ivb)
        {
            if (pass == null || ivb == null)
                throw new IllegalArgumentException("Missing one or more required arguments");

            if (ivb.length != 16)
                throw new IllegalArgumentException("Initialization vector must be 16 bytes");

            iv = new GCMParameterSpec(128, ivb);
            key = _try(() -> {
                KeySpec spec = new PBEKeySpec(pass.toCharArray(), ivb, 65536, 256);
                SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                return new SecretKeySpec(
                        skf.generateSecret(spec).getEncoded(), "AES"
                );
            }, "Crypto init failure");

            built = true;
            return this;
        }

        public byte[] encrypt(byte[] value)
        {
            if (!built)
                throw new IllegalStateException("Object not initialized");
            return _try(() -> {
                Cipher cipher = Cipher.getInstance(CIPHER_ALG);
                cipher.init(Cipher.ENCRYPT_MODE, key, iv);
                return cipher.doFinal(value);
            }, "Encryption failed");
        }

        public byte[] decrypt(byte[] value)
        {
            if (!built)
                throw new IllegalStateException("Object not initialized");
            return _try(() -> {
                Cipher cipher = Cipher.getInstance(CIPHER_ALG);
                cipher.init(Cipher.DECRYPT_MODE, key, iv);
                return cipher.doFinal(value);
            }, "Decryption failed");
        }

        public String encode(byte[] payload)
        {
            return flag + Base64.getEncoder().encodeToString(payload);
        }

        public byte[] decode(String value)
        {
            return Base64.getDecoder().decode(
                    value.replace(flag, "")
                        .getBytes(CHARSET)
            );
        }
    }

}
