#!/usr/bin/perl -w

$VERSION_FILE = "./utils/src/main/resources/version.properties";

open(F, "pom.xml") || die "unable to open pom.xml\n";
while (<F>) {
    if (/<version>([^<]+)/ && !/<!--/) {
	$version = $1;
	last;
    }
}
close(F);
die "unable to determine version\n" if (!defined($version));

open(F, "$VERSION_FILE")
  || die "unable to open $VERSION_FILE\n";
while (<F>) {
    if (/^serial=(\d+)/) {
	$serial = $1;
	last;
    }
}
close(F);
die "unable to determine serial\n" if (!defined($serial));
++$serial;

($mday, $mon, $year) = (localtime(time))[3, 4, 5];
$timestamp = sprintf("%04d-%02d-%02d", $year + 1900, $mon + 1, $mday);

open(F, "> $VERSION_FILE") || die "unable to write $VERSION_FILE\n";
print F "version=$version\n";
print F "timestamp=$timestamp\n";
print F "serial=$serial\n";

print "Saved version: $version, timestamp: $timestamp, serial: $serial\n";
