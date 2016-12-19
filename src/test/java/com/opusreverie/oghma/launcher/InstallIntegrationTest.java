package com.opusreverie.oghma.launcher;

import com.opusreverie.oghma.launcher.converter.Decoder;
import com.opusreverie.oghma.launcher.domain.Release;
import com.opusreverie.oghma.launcher.helper.PackTestUtil;
import com.opusreverie.oghma.launcher.helper.ReactiveResult;
import com.opusreverie.oghma.launcher.helper.ReleaseTestUtil;
import com.opusreverie.oghma.launcher.io.InstallProgressEvent;
import com.opusreverie.oghma.launcher.io.LocalReleaseRepository;
import com.opusreverie.oghma.launcher.io.ReleaseInstaller;
import com.opusreverie.oghma.launcher.io.download.FileDownloader;
import com.opusreverie.oghma.launcher.io.file.FileHandler;
import com.opusreverie.oghma.launcher.io.pack.PackExtractor;
import io.lyra.oghma.common.io.DirectoryResolver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Client;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Matchers.any;

/**
 * Integrated unpack and install test.
 *
 * @author Cian.
 */
@RunWith(MockitoJUnitRunner.class)
public class InstallIntegrationTest {

    private static final Path OGHMA_ROOT = Paths.get("any");

    private ReleaseInstaller installer;

    @Mock
    private FileHandler mockFileHandler;

    @Before
    public void setUp() throws Exception {
        // Behaviour
        Mockito.when(mockFileHandler.readAllBytes(any(Path.class))).thenReturn(new byte[]{1, 2});
        Mockito.when(mockFileHandler.getInputStream(any(Path.class))).then(__ -> PackTestUtil.createPackAsStream());
        Mockito.when(mockFileHandler.getOutputStream(any(Path.class))).then(__ -> new ByteArrayOutputStream());

        // Objects
        DirectoryResolver dirResolver = DirectoryResolver.ofRoot(OGHMA_ROOT);
        LocalReleaseRepository releaseRepository = new LocalReleaseRepository(new Decoder(), dirResolver, mockFileHandler);
        FileDownloader fileDownloader = new FileDownloader(dirResolver, mockFileHandler) {
            @Override
            protected InputStream retrieveHttpResponseStream(Client httpClient, String url) throws IOException {
                return PackTestUtil.createPackAsStream();
            }
        };
        PackExtractor extractor = new PackExtractor(mockFileHandler, dirResolver);
        installer = new ReleaseInstaller(fileDownloader, extractor, releaseRepository, dirResolver);
    }

    @Test
    public void testSuccessfulInstall() throws Throwable {
        // Given
        Release release = ReleaseTestUtil.createTestRelease(5, false);

        // When
        ReactiveResult<InstallProgressEvent> r = ReactiveResult.of(installer.install(release));

        // Then
        if (!r.getErrors().isEmpty()) throw r.getErrors().get(0);
        Assert.assertTrue(r.getCompleted().get());
        Assert.assertEquals(6 + (5 * 12), r.getEmitted().size()); // 1 binary and 5 packs (each with 12 files inside the pack)
        Assert.assertEquals(0, r.getErrors().size());
    }

}
