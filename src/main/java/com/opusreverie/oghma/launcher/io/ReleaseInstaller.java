package com.opusreverie.oghma.launcher.io;

import com.opusreverie.oghma.launcher.domain.Content;
import com.opusreverie.oghma.launcher.domain.Release;
import com.opusreverie.oghma.launcher.io.download.FileDownloader;
import com.opusreverie.oghma.launcher.io.download.ProgressEvent;
import com.opusreverie.oghma.launcher.io.file.DirectoryResolver;
import com.opusreverie.oghma.launcher.io.pack.PackExtractor;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    private final DirectoryResolver dirResolver;
    private final ConcurrentHashMap<Release, Observable<ProgressEvent>> fileLocks = new ConcurrentHashMap<>();

    public ReleaseInstaller(final LocalReleaseRepository releaseRepository, final DirectoryResolver dirResolver) {
        this(new FileDownloader(dirResolver), new PackExtractor(dirResolver), releaseRepository, dirResolver);
    }

    public ReleaseInstaller(final FileDownloader fileDownloader, final PackExtractor packExtractor,
                            final LocalReleaseRepository releaseRepository, final DirectoryResolver dirResolver) {
        this.fileDownloader = fileDownloader;
        this.packExtractor = packExtractor;
        this.releaseRepository = releaseRepository;
        this.dirResolver = dirResolver;
    }

    public Observable<ProgressEvent> install(final Release release) {

        final Observable<ProgressEvent> stream = Observable.<ProgressEvent>create(subscriber -> install(release, subscriber)).subscribeOn(Schedulers.io());

        if (lock(release, stream)) {
            return stream;
        }
        else {
            return Observable.error(new IllegalStateException("Download already running"));
        }
    }

    private void install(final Release release, final Subscriber<? super ProgressEvent> subscriber) {
        // Binary
        try {
            final Download download = new Download(release);

            release.getBinaryAndContent().forEach(content -> installFile(subscriber, download, content));

            try {
                releaseRepository.writeReleaseMeta(release);
            }
            catch (IOException e) {
                subscriber.onError(e);
            }

            subscriber.onCompleted();
        }
        finally {
            unlock(release);
        }
    }

    private void installFile(Subscriber<? super ProgressEvent> subscriber, Download download, Content file)
    {
        if (!subscriber.isUnsubscribed()) {
            final Action1<ProgressEvent> handler = prog -> subscriber
                    .onNext(download.updateProgress(file, prog.getDownloadedBytes()).getProgress());

            if (!dirResolver.isInstalled(file))
            {
                fileDownloader.downloadFile(file).subscribeOn(Schedulers.io()).toBlocking().subscribe(handler, subscriber::onError);

                if (!subscriber.isUnsubscribed())
                {
                    packExtractor.extract(file).subscribeOn(Schedulers.io()).toBlocking().subscribe(__ -> {}, subscriber::onError);
                }
            }
        }
    }

    private boolean lock(Release release, Observable<ProgressEvent> stream)
    {
        return fileLocks.putIfAbsent(release, stream) == null;
    }

    private void unlock(Release release)
    {
        fileLocks.remove(release);
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
