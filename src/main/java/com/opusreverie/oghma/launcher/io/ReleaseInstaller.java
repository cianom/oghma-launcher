package com.opusreverie.oghma.launcher.io;

import com.opusreverie.oghma.launcher.domain.Content;
import com.opusreverie.oghma.launcher.domain.Release;
import com.opusreverie.oghma.launcher.io.download.FileDownloader;
import com.opusreverie.oghma.launcher.io.download.ProgressEvent;
import com.opusreverie.oghma.launcher.io.pack.PackExtractor;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Installs content to the local filesystem from a remote source.
 * <p>
 * This includes downloading the content, hash validation and extracting any pack files.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class ReleaseInstaller {

    private final FileDownloader fileDownloader;
    private final PackExtractor packExtractor;
    private final LocalReleaseRepository releaseRepository;
    private final AtomicBoolean downloading = new AtomicBoolean(false);

    public ReleaseInstaller(final Path oghmaRoot, final LocalReleaseRepository releaseRepository) {
        this(new FileDownloader(oghmaRoot), new PackExtractor(oghmaRoot), releaseRepository);
    }

    public ReleaseInstaller(final FileDownloader fileDownloader, final PackExtractor packExtractor,
                            final LocalReleaseRepository releaseRepository) {
        this.fileDownloader = fileDownloader;
        this.packExtractor = packExtractor;
        this.releaseRepository = releaseRepository;
    }

    public Observable<ProgressEvent> install(final Release release) {
        boolean permitDownload = downloading.compareAndSet(false, true);
        if (!permitDownload)
            return Observable.error(new IllegalStateException("Download already running"));

        return Observable.<ProgressEvent>create(subscriber -> install(release, subscriber)).subscribeOn(Schedulers.io());
    }

    private void install(final Release release, final Subscriber<? super ProgressEvent> subscriber) {
        // Binary
        final Download download = new Download(release);

        for (final Content file : release.getBinaryAndContent()) {
            if (!subscriber.isUnsubscribed()) {
                final Action1<ProgressEvent> handler = prog -> subscriber
                        .onNext(download.updateProgress(file, prog.getDownloadedBytes()).getProgress());

                fileDownloader.downloadFile(file)
                        .subscribeOn(Schedulers.io())
                        .toBlocking()
                        .subscribe(handler, subscriber::onError);

                packExtractor.extract(file)
                        .subscribeOn(Schedulers.io())
                        .toBlocking()
                        .subscribe(handler, subscriber::onError);
            }
        }

        try {
            releaseRepository.writeReleaseMeta(release);
        }
        catch (IOException e) {
            subscriber.onError(e);
        }


        subscriber.onCompleted();
        downloading.set(false);
    }

    public static class Download {

        private final Map<Content, Long> progress;

        public Download(final Release release) {
            this.progress = release.getBinaryAndContent().stream().collect(Collectors.toMap(c1 -> c1, c2 -> 0L));
        }

        public Download updateProgress(final Content file, final long downloadedBytes) {
            progress.put(file, downloadedBytes);
            return this;
        }

        public ProgressEvent getProgress() {
            final long downloadedBytes = progress.values().stream().mapToLong(l -> l).sum();
            final long totalBytes = progress.keySet().stream().mapToLong(Content::getSizeBytes).sum();
            return new ProgressEvent(downloadedBytes, totalBytes);
        }

    }

}
