
package edu.ucla.library.iiif.kakadu;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.testcontainers.containers.Container;

import info.freelibrary.util.Logger;
import info.freelibrary.util.LoggerFactory;

/**
 * Tests of the Kakadu container.
 */
public class KakaduFT extends AbstractKakaduFT {

    private static final Logger LOGGER = LoggerFactory.getLogger(KakaduFT.class, MessageCodes.BUNDLE);

    private static final String KDU_COMPRESS = "/usr/local/bin/kdu_compress";

    private static final String KDU_EXPAND = "/usr/local/bin/kdu_expand";

    private static final String PRINTENV = "printenv";

    private static final String DASH_I = "-i";

    private static final String DASH_O = "-o";

    private static final String LIST = "ls";

    /**
     * Tests that the kakadu home directory has been created.
     *
     * @throws InterruptedException If the test process gets interrupted
     * @throws IOException If there are i/o issues during the test
     */
    @Test
    public void testKakaduHomeDir() throws InterruptedException, IOException {
        final Container.ExecResult result = myTestContainer.execInContainer(LIST, "/home/");

        assertTrue(result.getExitCode() == 0);
        assertTrue(LOGGER.getMessage(MessageCodes.KDU_001, "/home/kakadu"), result.getStdout().contains("kakadu"));
    }

    /**
     * Tests that the JAVA_HOME environmental property has been set correctly.
     *
     * @throws InterruptedException If the test process gets interrupted
     * @throws IOException If there are i/o issues during the test
     */
    @Test
    public void testJavaHomeEnvProperty() throws InterruptedException, IOException {
        final Container.ExecResult result = myTestContainer.execInContainer(PRINTENV);

        assertTrue(result.getExitCode() == 0);
        assertTrue(result.getStdout().contains("JAVA_HOME=/usr/lib/jvm"));
    }

    /**
     * Tests that the LD_LIBRARY_PATH environmental property has been set correctly.
     *
     * @throws InterruptedException If the test process gets interrupted
     * @throws IOException If there are i/o issues during the test
     */
    @Test
    public void testLdLibraryPathEnvProperty() throws InterruptedException, IOException {
        final Container.ExecResult result = myTestContainer.execInContainer(PRINTENV);

        assertTrue(result.getExitCode() == 0);
        assertTrue(result.getStdout().contains("LD_LIBRARY_PATH=/usr/lib/jni"));
    }

    /**
     * Tests that our test images have been mounted in the container.
     *
     * @throws InterruptedException If the test process gets interrupted
     * @throws IOException If there are i/o issues during the test
     */
    @Test
    public void testTestImagesMount() throws InterruptedException, IOException {
        final Container.ExecResult result = myTestContainer.execInContainer(LIST, "/images");

        assertTrue(result.getExitCode() == 0);
        assertTrue(result.getStdout().contains("test.tif"));
    }

    /**
     * Tests that a TIFF image can be converted into a JP2.
     *
     * @throws InterruptedException If the test process gets interrupted
     * @throws IOException If there are i/o issues during the test
     */
    @Test
    public void testKduCompress() throws InterruptedException, IOException {
        final Container.ExecResult result =
                myTestContainer.execInContainer(KDU_COMPRESS, DASH_I, "/images/test.tif", DASH_O, "/tmp/test.jp2");

        assertTrue(result.getExitCode() == 0);
    }

    /**
     * Tests that a JP2 image can be converted into a TIFF.
     *
     * @throws InterruptedException If the test process gets interrupted
     * @throws IOException If there are i/o issues during the test
     */
    @Test
    public void testKduExpand() throws InterruptedException, IOException {
        final Container.ExecResult result =
                myTestContainer.execInContainer(KDU_EXPAND, DASH_I, "/images/test.jp2", DASH_O, "/tmp/test.tiff");

        assertTrue(result.getExitCode() == 0);
    }

    /**
     * Tests that kdu_compress version returns the expected version number.
     *
     * @throws InterruptedException If the process gets interrupted
     * @throws IOException If there are i/o issues during the test
     */
    @Test
    public void testKduCompressVersion() throws InterruptedException, IOException {
        final Container.ExecResult result = myTestContainer.execInContainer(KDU_COMPRESS, "-version");
        final String kduVersion = parseKduVersion(System.getProperty("kdu.version"));

        assertTrue(result.getExitCode() == 0);
        assertTrue(result.getStdout().contains(kduVersion));
    }

}
