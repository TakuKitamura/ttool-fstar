# Top LaTeX document (without .tex extension)
TOP		= users_guide.tex
# Bibliography (if any)
BIBLIO		= 

SHELL		= /bin/bash

# Figures directory
FIGDIR		= fig

# Maximum number of pdflatex compilations
MAXPASSES	?= 5

# Main, common, Makefile
include ../mli.mk
#include Makefile.inc

# Main PDF depends on other PDFs generated from fig, dot, dia, gnuplot or
# inkscape formats. The GENERATED make variable is computed automatically by
# Makefile.inc, based on what is found in the FIGDIR directory.
$(TOP).pdf:
	

# Specific dependencies
$(TOP).pdf: $(wildcard *.tex)

