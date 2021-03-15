package zen.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class TestCrypto {
    private static final String FLAG = Crypto.FLAG;
    private static final Crypto.Module G_MOD = new Crypto.GCMModule("{g}");

    @BeforeAll public static void before()
    {
        G_MOD.build("test-password", "test.code-secret".getBytes(StandardCharsets.UTF_8));
        Crypto.set(G_MOD);
    }
    @AfterAll public static void after() {Crypto.destroy();}

    @Test public void testInit() {
        Exception e = assertThrows(IllegalStateException.class, Crypto::new);
        assertEquals("Crypto cannot be initialized", e.getMessage());
    }

    @Test public void test() {
        String original = "1718021917",
                expected = "{g}eqG0TFRJnCr+QKCGLCzZ4LUehlbWEukL7tc=",
                base = FLAG + "xNEzq1cplajxFMfKtHhZQniFgbkQphGY/WoYZ4UHIwa01zz9Eg==",
                cenc = "{c}lpqNY1a4Yu7P84elNyhMmQ==";
        String b64 = Crypto.encrypt(original);

        assertEquals(expected, b64);
        assertEquals(original, Crypto.decrypt(expected));
        assertEquals(cenc, Crypto.decrypt(cenc));
        assertEquals("", Crypto.decrypt(""));
        assertNull(Crypto.decrypt(null));
        assertNull(Crypto.encrypt(null));
        assertNull(Crypto.encode(null));
        assertNull(Crypto.decode(null));

        assertEquals(base, Crypto.encode(base));
        assertEquals("{g}fq6wRFJKn2FXgIDVYvdbNVrg9WMlHnA=", Crypto.encrypt("5850612"));
    }

    @Test public void testHeadlessEncoding()
    {
        String value = "hello",
                base = FLAG + "Ol/DGmt7gOtQTsHFcCLZrN3KN9PhD4K8GPYlAvfwwINERYnbgw=="
        ;
        String enc = Crypto.encode("{aesd}", value);

        assertNull(Crypto.encode(null));
        assertEquals(value, Crypto.decode("{aesd}", enc));
        assertEquals(value, Crypto.decode(base));
        assertNull(Crypto.decode("{plain}", null));
    }

    @Test public void testUninitializedCrypto() {
        String original = "1718021917",
                expected = "{g}eqG0TFRJnCr+QKCGLCzZ4LUehlbWEukL7tc=",
                base = FLAG + "xNEzq1cplajxFMfKtHhZQniFgbkQphGY/WoYZ4UHIwa01zz9Eg==",
                cenc = "{c}lpqNY1a4Yu7P84elNyhMmQ==";

        Crypto.destroy();
        String b64 = Crypto.encrypt(original);

        assertNull(Crypto.encrypt(null));

        assertEquals(original, b64);
        assertEquals(expected, Crypto.decrypt(expected));
        assertEquals(cenc, Crypto.decrypt(cenc));
        assertEquals("", Crypto.decrypt(""));
        assertNull(Crypto.decrypt(null));
        assertNull(Crypto.encrypt(null));
        assertNull(Crypto.encode(null));

        assertEquals(base, Crypto.encode(base));
        assertEquals("5850612", Crypto.encrypt("5850612"));

        Crypto.set(G_MOD);
    }

    @Test public void testCredentials() {
        String pass = "test-password";
        String iv = "test.code-secret";
        String encpass = FLAG + "iwOAnDol5Dge8hVkM0MaAzgqQMg6EHwU8nngkgl5jOjNgnISWixuWKmyfaW9";
        String enciv = FLAG + "A6Z/y7loh7tDYTQbwX+dd2jbxjiBnWfwutwFfRWEOfvHdlwoGkfvO1OYV9eaRC5C";

        byte[] ivb = iv.getBytes(StandardCharsets.UTF_8);

        Crypto.Credential cred = new Crypto.Credential(pass, iv);
        assertEquals(pass, cred.password());
        assertArrayEquals(ivb, cred.iv());

        cred = new Crypto.Credential(encpass, enciv);
        assertEquals(pass, cred.password());
        assertArrayEquals(ivb, cred.iv());
    }

    @Test public void testModule() {
        Crypto.Module cm = new Crypto.GCMModule("{g}");
        byte[] val = "Hello".getBytes(StandardCharsets.UTF_8);

        Exception e;
        e = assertThrows(IllegalStateException.class, () -> new Crypto.GCMModule(null));
        assertEquals("Crypto module must have identifier", e.getMessage());

        e = assertThrows(IllegalStateException.class, () -> cm.encrypt(val));
        assertEquals("Object not initialized", e.getMessage());

        e = assertThrows(IllegalStateException.class, () -> cm.decrypt(val));
        assertEquals("Object not initialized", e.getMessage());

        e = assertThrows(IllegalArgumentException.class, () -> cm.build(null, null));
        assertEquals("Missing one or more required arguments", e.getMessage());

        e = assertThrows(IllegalArgumentException.class, () -> cm.build("Hello", null));
        assertEquals("Missing one or more required arguments", e.getMessage());

        e = assertThrows(IllegalArgumentException.class, () -> cm.build(null, "Hello".getBytes()));
        assertEquals("Missing one or more required arguments", e.getMessage());

        e = assertThrows(IllegalArgumentException.class, () -> cm.build("Hello", "Hello".getBytes()));
        assertEquals("Initialization vector must be 16 bytes", e.getMessage());
    }

    @Test public void test_try() {
        Exception e = assertThrows(RuntimeException.class, () ->
            Crypto._try(() -> {throw new IllegalStateException("Test");}, "testing")
        );
        assertEquals(RuntimeException.class, e.getClass());
        assertEquals("testing", e.getMessage());
        assertEquals(IllegalStateException.class, e.getCause().getClass());
        assertEquals("Test", e.getCause().getMessage());
    }
}
