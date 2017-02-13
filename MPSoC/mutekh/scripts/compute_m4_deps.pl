#!/usr/bin/perl

use strict;
use File::Path;

my $dest = shift @ARGV;
my @inc_paths;
my %files;

while (defined (my $s = shift @ARGV)) {
    my $p;
    if ( $s =~ /^-I$/ ) {
        $p = shift @ARGV;
    } elsif ( $s =~ /^-I(.+)$/ ) {
        $p = $1;
    } else {
        next;
    }
    push @inc_paths, Cwd::realpath($p) if (-d $p);
}

foreach (<STDIN>) {
    if ( /^#line\s+\d+\s+"(.*)"\s*$/ ) {
        $files{$1}++;
    }
}

print "$dest:";
foreach my $f (keys(%files)) {
    foreach my $p (@inc_paths) {
        if ( -f "$p/$f" ) {
            print "\\\n\t$p/$f";
        }
    }
}
print "\n";

