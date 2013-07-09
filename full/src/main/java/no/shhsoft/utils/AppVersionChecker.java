package no.shhsoft.utils;

import java.net.MalformedURLException;
import java.net.URL;

import no.shhsoft.thread.DaemonThread;

/**
 * Assumes that <code>AppProps</code> have been loaded, and that
 * they will be saved by someone else.
 *
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class AppVersionChecker
extends DaemonThread {

    private static final String PROP_PREFIX = "versionChecker.";
    private static final String PROP_LAST_CHECK = PROP_PREFIX + "lastCheckMinute";
    private static final String PROP_INTERVAL = PROP_PREFIX + "checkIntervalMinutes";
    private static final String PROP_ENABLED = PROP_PREFIX + "enabled";
    private static final String PROP_LAST_REPORTED_SERIAL = PROP_PREFIX + "lastReportedSerial";
    private static final long DEFAULT_CHECK_INTERVAL_MINUTES = 5L * 60L;
    private final URL latestVersionUrl;
    private final VersionData installedVersion;
    private VersionData latestVersion;
    private long checkIntervalMinutes;
    private long lastCheckMinute = 0L;
    private int lastReportedSerial;
    private boolean enabled;

    private void downloadLatestVersionData() {
        VersionData downloadedVersionData = null;
        try {
            downloadedVersionData = VersionData.getVersionDataFromUrl(latestVersionUrl);
        } catch (final Exception e) {
            System.err.println("Error checking for availability of new version: " + e.getMessage());
        }
        synchronized (this) {
            if (downloadedVersionData != null) {
                latestVersion = downloadedVersionData;
            }
        }
    }

    public AppVersionChecker(final String baseUrl) {
        AppProps.setDefault(PROP_ENABLED, true);
        AppProps.setDefault(PROP_INTERVAL, DEFAULT_CHECK_INTERVAL_MINUTES);
        String base = baseUrl;
        if (!base.endsWith("/")) {
            base += "/";
        }
        try {
            latestVersionUrl = new URL(base + VersionData.VERSION_PROPERTIES_NAME);
        } catch (final MalformedURLException e) {
            throw new UncheckedIoException(e);
        }
        installedVersion = VersionData.getApplicationVersionData();
        lastCheckMinute = AppProps.getLong(PROP_LAST_CHECK);
        checkIntervalMinutes = AppProps.getLong(PROP_INTERVAL);
        lastReportedSerial = AppProps.getInt(PROP_LAST_REPORTED_SERIAL);
        enabled = AppProps.getBoolean(PROP_ENABLED);
        start();
    }

    /**
     * @return <code>true</code> if a version is available that is newer than
     *     the last time this method returned <code>true</code>.
     */
    public synchronized boolean isNewVersionAvailable() {
        if (latestVersion != null) {
            final int latestVersionSerial = latestVersion.getSerial();
            if (latestVersionSerial > lastReportedSerial
                && latestVersionSerial > installedVersion.getSerial()) {
                setLastReportedSerial(latestVersionSerial);
                return true;
            }
        }
        return false;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
        AppProps.set(PROP_ENABLED, enabled);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setCheckIntervalMinutes(final long checkIntervalMinutes) {
        this.checkIntervalMinutes = checkIntervalMinutes;
        AppProps.set(PROP_INTERVAL, checkIntervalMinutes);
        interrupt();
    }

    public long getCheckIntervalMinutes() {
        return checkIntervalMinutes;
    }

    void setLastCheckMinute(final long lastCheckMinute) {
        this.lastCheckMinute = lastCheckMinute;
        AppProps.set(PROP_LAST_CHECK, lastCheckMinute);
        interrupt();
    }

    public long getLastCheckMinute() {
        return lastCheckMinute;
    }

    void setLastReportedSerial(final int lastReportedSerial) {
        this.lastReportedSerial = lastReportedSerial;
        AppProps.set(PROP_LAST_REPORTED_SERIAL, lastReportedSerial);
    }

    public int getLastReportedSerial() {
        return lastReportedSerial;
    }

    @Override
    public void run() {
        while (!shouldStop()) {
            final long nextCheckMs = (lastCheckMinute + checkIntervalMinutes) * 60L * 1000L;
            final long delayMs = nextCheckMs - System.currentTimeMillis();
            if (delayMs > 0L) {
                try {
                    Thread.sleep(delayMs);
                } catch (final InterruptedException e) {
                    continue;
                }
            }
            setLastCheckMinute(System.currentTimeMillis() / (60L * 1000L));
            if (isEnabled()) {
                downloadLatestVersionData();
            }
        }
    }

}
