
package edu.ucla.library.iiif.kakadu;

import org.junit.After;
import org.junit.Before;
import org.testcontainers.containers.GenericContainer;

/**
 * Underlying test functionality.
 */
public abstract class AbstractKakaduFT {

    /**
     * Spins up a Kakadu test container. This is lightweight enough that we can do it for every test instead of using
     * the singleton method.
     */
    @SuppressWarnings("rawtypes")
    protected final GenericContainer myTestContainer = new GenericContainer(getImageTag());

    /**
     * Creates a new abstract functional test.
     */
    protected AbstractKakaduFT() {
    }

    /**
     * Sets up testing environment.
     */
    @Before
    public void setUp() {
        myTestContainer.withFileSystemBind("src/test/resources/images/", "/images/");
        myTestContainer.start();
    }

    /**
     * Cleans up the testing environment.
     */
    @After
    public void tearDown() {
        myTestContainer.stop();
    }

    /**
     * Gets the kdu_compress version from the supplied project version.
     *
     * @param aProjectVersion A project version
     * @return A kdu_compress version
     */
    protected String parseKduVersion(final String aProjectVersion) {
        final int index = aProjectVersion.indexOf('-'); // we delineate our version from Kakadu's with a dash
        return aProjectVersion.substring(0, index);
    }

    /**
     * Replaces a SNAPSHOT version with 'latest' for the Docker image tag.
     *
     * @return A Docker image tag
     */
    private String getImageTag() {
        final String tag = System.getProperty("container.tag");

        if (tag.contains("-SNAPSHOT")) {
            final StringBuilder builder = new StringBuilder(tag);
            final int index = builder.lastIndexOf(":");

            if (index != -1) {
                return builder.replace(index + 1, builder.length(), "latest").toString();
            } else {
                return tag;
            }
        } else {
            return tag;
        }
    }

}
