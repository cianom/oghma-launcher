package com.opusreverie.oghma.launcher.io.pack;

import com.opusreverie.oghma.launcher.domain.Content;
import com.opusreverie.oghma.launcher.helper.ReactiveResult;
import com.opusreverie.oghma.launcher.io.file.FileHandler;
import io.lyra.oghma.common.io.DirectoryResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link PackExtractor}.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
@RunWith(MockitoJUnitRunner.class)
public class PackExtractorTest {

    private static final Content FILE = new Content("www.any.com", "testpack.op1", 123, "123abc");

    private PackExtractor classUnderTest;

    @Mock
    private FileHandler mockFileHandler;

    @Before
    public void setUp() throws Exception {
        classUnderTest = new PackExtractor(mockFileHandler, DirectoryResolver.ofRoot(Paths.get("/pack")));

        when(mockFileHandler.exists(any(Path.class))).thenReturn(true, false);
    }

    @Test
    public void testExtract_success() throws Throwable {
        // Given
        when(mockFileHandler.getInputStream(any(Path.class))).then(invocationOnMock ->
                PackExtractorTest.class.getResourceAsStream(invocationOnMock.getArguments()[0].toString()));

        // When
        ReactiveResult r = ReactiveResult.of(classUnderTest.extract(FILE));

        // Then
        r.throwAny();
        assertTrue(r.getCompleted().get());
        assertTrue(r.getErrors().isEmpty());
        verify(mockFileHandler, times(2)).write(any(Path.class), any(byte[].class));
        verify(mockFileHandler).createFile((any(Path.class)));
    }

    @Test
    public void testExtract_failureReadingPack() throws Exception {
        // Given
        when(mockFileHandler.getInputStream(any(Path.class))).thenThrow(new IOException("Err"));
        final AtomicInteger errorCounter = new AtomicInteger(0);

        // When
        classUnderTest.extract(FILE).toBlocking()
                .subscribe(__ -> {
                }, ex -> errorCounter.incrementAndGet());

        // Then
        assertEquals(1, errorCounter.get());
        verify(mockFileHandler, never()).write(any(Path.class), any(byte[].class));
        verify(mockFileHandler, never()).move(any(Path.class), any(Path.class));
    }

    @Test
    public void testExtract_failureWritingToDisk_cleanUpFiles() throws Exception {
        // Given
        when(mockFileHandler.getInputStream(any(Path.class))).then(invocationOnMock ->
                PackExtractorTest.class.getResourceAsStream(invocationOnMock.getArguments()[0].toString()));
        when(mockFileHandler.write(any(Path.class), any(byte[].class)))
                .thenReturn(null).thenThrow(new IOException("Err"));
        when(mockFileHandler.exists(any(Path.class))).thenReturn(true, false, false);
        final AtomicInteger errorCounter = new AtomicInteger(0);

        // When
        classUnderTest.extract(FILE).toBlocking()
                .subscribe(__ -> {
                }, ex -> errorCounter.incrementAndGet());

        // Then
        assertEquals(1, errorCounter.get());
        verify(mockFileHandler, times(2)).write(any(Path.class), any(byte[].class));
        verify(mockFileHandler).delete(any(Path.class));
        verify(mockFileHandler, never()).move(any(Path.class), any(Path.class));

    }

}