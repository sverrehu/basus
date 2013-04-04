#!/usr/bin/perl -w

sub readMessageKeys {
    my($filename) = @_;
    my(%keys) = ();
    open(F, $filename) || die "unable to open $filename\n";
    while (<F>) {
	if (/^([^=]+)=/) {
	    $key = $1;
	    $keys{$key} = 1;
	}
    }
    close(F);
    return %keys;
}

%messages = &readMessageKeys("./src/main/resources/messages.properties");
%messagesNb = &readMessageKeys("./src/main/resources/messages_nb.properties");

print "Keys in messages.properties that are not in messages_nb.properties\n";
foreach $key (sort keys %messages) {
    defined($messagesNb{$key}) || print "    $key\n";
}
print "\n\nKeys in messages_nb.properties that are not in messages.properties\n";
foreach $key (sort keys %messagesNb) {
    defined($messages{$key}) || print "    $key\n";
}


$java = "";
open(P, "find ./src -name \*.java | xargs cat |") || die "unable to pipe\n";
while (<P>) {
    $java .= $_;
}
close(P);
print "\n\nKeys that appear to be unused\n";

foreach $key (sort keys %messages) {
    if (!($java =~ /"$key"/) && !($key =~ /^example\..*\.bus/) && !($key =~ /^preferences\.language\..*/)) {
        print "    $key\n";
    }
}

print "\n\nTODO statements in code\n";
open(P, "find src/main/java -type f \\! -path \*/.svn/\\* | xargs grep TODO |");
while (<P>) {
    chomp;
    print "    $_\n";
}
close(P);

print "\n\nTODO statements in resources\n";
open(P, "find src/main/resources -type f \\! -path \*/.svn/\\* | xargs grep TODO |");
while (<P>) {
    chomp;
    print "    $_\n";
}
close(P);

print "\n\nMissing Help page translations\n";
system("(cd src/main/resources/help >/dev/null; for q in `find . -name \\*.html \\! -path \\*/nb/\\* | sort`; do test -f nb/\$q || echo \"    \$q\"; done)");

print "\n\nMissing function help pages\n";
open(P, "find src/main/java -name \\*.java \\! -path \*/.svn/\\* | xargs grep \"private static final Function\" |");
while (<P>) {
    $name = lc((/.*Function\s+([A-Za-z0-9_]+)/)[0]);
    $name =~ s/_([a-z])/\u$1/g;
    if (! -f "src/main/resources/help/func/$name.html"
	&& $name ne "acosr"
	&& $name ne "asinr"
	&& $name ne "atanr"
	&& $name ne "cosr"
	&& $name ne "getBlue"
	&& $name ne "getGreen"
	&& $name ne "mouseY"
	&& $name ne "sinr"
	&& $name ne "spriteY"
	&& $name ne "tanr"
	&& $name ne "toUpper"
	) {
	print "    $name\n";
    }
}
close(P);

print "\n\nMissing functions in function index page\n";
open(P, "find src/main/resources/help/func -name \\*.html \\! -path \*/.svn/\\* |");
@funcFiles = ();
while (<P>) {
    chomp;
    s/.*\///;
    s/\.html//;
    push(@funcFiles, $_);
}
close(P);
$funcIndex = "";
open(F, "src/main/resources/help/functions.html");
while (<F>) {
    $funcIndex .= $_;
}
close(F);
foreach $funcFile (@funcFiles) {
    if (!($funcIndex =~ /\"func\/$funcFile\"/)) {
	print "    $funcFile\n";
    }
}
