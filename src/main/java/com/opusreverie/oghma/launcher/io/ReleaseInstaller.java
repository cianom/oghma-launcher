package com.opusreverie.oghma.launcher.io;

import com.opusreverie.oghma.launcher.domain.Content;
import com.opusreverie.oghma.launcher.domain.Release;
import com.opusreverie.oghma.launcher.io.download.FileDownloader;
import com.opusreverie.oghma.launcher.io.pack.PackExtractor;
import io.lyra.oghma.common.content.ContentType;
import io.lyra.oghma.common.io.DirectoryResolver;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.opusreverie.oghma.launcher.io.download.FileDownloader.DownloadProgressEvent;

/**
 * Installs content to the local filesystem from a remote source.
 * <p>
 * This includes downloading the content, hash validation and extracting any pack files.
 *
 * @author Cian.
 */
public class ReleaseInstaller {

    private final FileDownloader                                               fileDownloader;
    private final PackExtractor                                                packExtractor;
    private final LocalReleaseRepository                                       releaseRepository;
    private final DirectoryResolver                                            dirResolver;
    private final ConcurrentHashMap<Release, Observable<InstallProgressEvent>> fileLocks;

    public ReleaseInstaller(final LocalReleaseRepository releaseRepository, final DirectoryResolver dirResolver) {
        this(new FileDownloader(dirResolver), new PackExtractor(dirResolver), releaseRepository, dirResolver);
    }

    public ReleaseInstaller(final FileDownloader fileDownloader, final PackExtractor packExtractor,
                            final LocalReleaseRepository releaseRepository, final DirectoryResolver dirResolver) {
        this.fileLocks = new ConcurrentHashMap<>();
        this.fileDownloader = fileDownloader;
        this.packExtractor = packExtractor;
        this.releaseRepository = releaseRepository;
        this.dirResolver = dirResolver;
    }

    public Observable<InstallProgressEvent> install(final Release release) {

        final Observable<InstallProgressEvent> stream = Observable
                .<InstallProgressEvent>create(subscriber -> install(release, subscriber))
                .subscribeOn(Schedulers.io());

        if (lock(release, stream)) {
            return stream;
        }
        else {
            return Observable.error(new IllegalStateException("Download already running"));
        }
    }

    private void install(final Release release, final Subscriber<? super InstallProgressEvent> subscriber) {
        try {
            final Install install = new Install(release);

            release.getBinaryAndContent().forEach(content -> installFile(subscriber, install, content));

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

    private void installFile(final Subscriber<? super InstallProgressEvent> subscriber,
                             final Install install, final Content file) {
        if (!subscriber.isUnsubscribed()) {
            final Action1<DownloadProgressEvent> handler = prog -> subscriber
                    .onNext(install.updateDownloadProgress(file, prog.getDownloadedBytes()).getProgress());

            fileDownloader.downloadFile(file)
                    .subscribeOn(Schedulers.io())
                    .toBlocking()
                    .subscribe(handler, subscriber::onError);

            if (isExtractable(file, subscriber)) {

                packExtractor.extract(file)
                        .subscribeOn(Schedulers.io())
                        .toBlocking()
                        .subscribe(s -> subscriber.onNext(install.updateExtractProgress(file).getProgress()),
                                subscriber::onError);
            }
        }
    }

    private boolean isExtractable(final Content file, final Subscriber<?> subscriber) {
        return !dirResolver.isInstalled(file.getPath(), file.getSha256Hash())
                && !subscriber.isUnsubscribed()
                && ContentType.isPack(file.getPath());
    }

    private boolean lock(final Release release, final Observable<InstallProgressEvent> stream) {
        return fileLocks.putIfAbsent(release, stream) == null;
    }

    private void unlock(final Release release) {
        fileLocks.remove(release);
    }

    private static class Install {

        private final Map<Content, Long> progress;
        private final Set<Content> extracted = new HashSet<>();

        Install(final Release release) {
            this.progress = release.getBinaryAndContent().stream().collect(Collectors.toMap(c1 -> c1, c2 -> 0L));
        }

        Install updateDownloadProgress(final Content file, final long downloadedBytes) {
            progress.put(file, downloadedBytes);
            return this;
        }

        Install updateExtractProgress(final Content file) {
            extracted.add(file);
            return this;
        }

        InstallProgressEvent getProgress() {
            final long downloadedBytes = progress.values().stream().mapToLong(l -> l).sum();
            final long totalBytes = progress.keySet().stream().mapToLong(Content::getSizeBytes).sum();
            final double downloadPercentage = ((double) downloadedBytes) / ((double) totalBytes);

            final double extractPercentage = ((double) extracted.size()) / ((double) progress.size());

            return new InstallProgressEvent(downloadPercentage, extractPercentage);
        }

    }

}
