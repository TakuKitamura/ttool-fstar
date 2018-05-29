#!/usr/bin/perl

use Getopt::Long;
use strict;

my ( $help, $name, $align, $section, $size, $output, $input )
    = ( 0, "blob", 1, undef, 0, "-" );

Getopt::Long::Configure("no_ignore_case");
my $r = GetOptions("n=s" => \$name, "a=i" => \$align,
                   "s=s" => \$section, "S" => \$size,
                   "o=s" => \$output, "h" => \$help);

if ( $help ) {
    print STDERR 
"usage: blob2c.pl [ -n symbol_name ] [ -s symbol_section ] blob > output
    -n name      Change symbol name (default = 'blob')
    -a value     Add an alignment attribute on blob variable
    -s name      Change section name (no default (.rodata))
    -S           Also emit a size symbol '<name>_size'
    -o name      Change section output file name (default stdout)
";
    exit 1;
}

$input = shift @ARGV;
if ( defined $input and $input ne "-" ) {
    close( STDIN );
    open( STDIN, "<$input" ) or die 'unable to open input file';
}

if ( $output ne "-" ) {
    close( STDOUT );
    open( STDOUT, ">$output" ) or die 'unable to open output file';
}

print "#include <stdint.h>\n" if ( $size );
print "__attribute__((section \"$section\"))\n" if ( defined $section );
print "__attribute__((aligned ($align)))\n" if ( defined $align );
print "const unsigned char $name\[\] = {\n";

my ( $tlen, $line ) = ( 0 );
while ( my $len = read( STDIN, $line, 16 ) ) {
    $line = unpack("H*", $line);
    $line =~ s/(..)/0x\1, /g;
    print "  $line\n";
    $tlen += $len;
}

print "};\n";

if ( $size ) {
    print "__attribute__((section \"$section\"))\n" if ( defined $section );
    print "const size_t ${name}_size = $tlen;\n"
}

exit 0;

