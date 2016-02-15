package com.opusreverie.oghma.launcher.io.download;

import com.opusreverie.oghma.launcher.domain.Content;
import com.opusreverie.oghma.launcher.domain.Release;
import com.opusreverie.oghma.launcher.io.pack.PackExtractor;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Created by keen on 07/02/16.
 */
public class DownloadsController {

    private AtomicBoolean downloading = new AtomicBoolean(false);

    private final FileDownloader fileDownloader;

    private final PackExtractor packExtractor;

    public DownloadsController(final Path oghmaRoot) {
        fileDownloader = new FileDownloader(oghmaRoot);
        packExtractor = new PackExtractor(oghmaRoot);
    }

    public Observable<ProgressEvent> download(final Release release) {
        boolean permitDownload = downloading.compareAndSet(false, true);
        if (!permitDownload) return Observable.error(new IllegalStateException("Download already running"));

        return Observable.<ProgressEvent>create(subscriber -> download(release, subscriber))
                .subscribeOn(Schedulers.io());
    }

    private void download(final Release release, final Subscriber<? super ProgressEvent> subscriber) {
        // Binary
        final Download download = new Download(release);

        for (final Content file : release.getBinaryAndContent()) {
            if (!subscriber.isUnsubscribed()) {
                final Action1<ProgressEvent> handler = prog -> subscriber.onNext(download
                        .updateProgress(file, prog.getDownloadedBytes())
                        .getProgress());

                fileDownloader.downloadFile(file)
                        .subscribeOn(Schedulers.io())
                        .toBlocking()
                        .subscribe(handler, subscriber::onError);

                packExtractor.extract(file)
                        .subscribeOn(Schedulers.io())
                        .toBlocking()
                        .subscribe(handler,subscriber::onError);
            }
        }
        subscriber.onCompleted();
        downloading.set(false);
    }

    public static class Download {

        private final Map<Content, Long> progress;

        public Download(final Release release) {
            this.progress = release.getBinaryAndContent().stream()
                    .collect(Collectors.toMap(c1 -> c1, c2 -> 0L));
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
