###########################################################
#                 COPYRIGHT                               #
#                 ---------                               #
#                                                         #
# See Copyright Notice in COPYING and license in LICENSE. #
###########################################################

#######################
# Adapt to your needs #
#######################

# mli needs bash
SHELL		:= /bin/bash
# Default max number of compilations
MAXPASSES	?= 30
# Default figures' sub-directory
FIGDIR		?= $(wildcard figures)
# Default bibliography
BIBLIO		?= $(wildcard biblio.bib)
# Log file for $(GENERATED) productions
GENERATEDLOG	?= generated.log

#########################################################
# Normally you shouldn't have to change anything below. #
# Unless you know what you are doing, of course.        #
#########################################################

# SHELL		= /bin/bash
TEXTOPS		= $(shell grep -l '[^%]*\\begin{document}' *.tex)
PDFTARGETS	= $(patsubst %.tex,%.pdf,$(TEXTOPS))
TARGETS		= $(patsubst %.tex,%,$(TEXTOPS))

# Tools
PDFLATEX	= pdflatex
PDFLATEXFLAGS	= -halt-on-error -interaction=batchmode
BIBTEX		= bibtex
BIBTEXFLAGS	=
FIG2DEV		?= $(shell which fig2dev 2> /dev/null)
FIG2DEVFLAGS	=
DOT		?= $(shell which dot 2> /dev/null)
DOTFLAGS	= -Tpdf
DIA		?= $(shell which dia 2> /dev/null)
DIAFLAGS	= -t eps-pango
EPSTOPDF	?= $(shell which epstopdf 2> /dev/null)
EPSTOPDFFLAGS	=
GNUPLOT		?= $(shell which gnuplot 2> /dev/null)
GNUPLOTFLAGS	=
INKSCAPE	?= $(shell which inkscape 2> /dev/null)
INKSCAPEFLAGS	=

# Files, path...
XFIGSRCS	= $(wildcard $(FIGDIR)/*.fig)
XFIGPDFS	= $(patsubst %.fig,%.pdf,$(XFIGSRCS))
XFIGPDFTS	= $(patsubst %.fig,%.pdf_t,$(XFIGSRCS))
DOTSRCS		= $(wildcard $(FIGDIR)/*.dot)
DOTPDFS		= $(patsubst %.dot,%.pdf,$(DOTSRCS))
DIASRCS		= $(wildcard $(FIGDIR)/*.dia)
DIAEPSS		= $(patsubst %.dia,%.eps,$(DIASRCS))
DIAPDFS		= $(patsubst %.dia,%.pdf,$(DIASRCS))
INKSCAPESRCS	= $(wildcard $(FIGDIR)/*.svg)
INKSCAPEPDFS	= $(patsubst %.svg,%.pdf,$(INKSCAPESRCS))
GNUPLOTSRCS	= $(wildcard $(FIGDIR)/*.gnuplot)
GNUPLOTPDFS	= $(patsubst %.gnuplot,%.pdf,$(GNUPLOTSRCS))
EPSSRCS		= $(filter-out $(DIAEPSS),$(wildcard $(FIGDIR)/*.eps))
EPSPDFS		= $(patsubst %.eps,%.pdf,$(EPSSRCS))

# Tex files
TEXFILES	= $(wildcard *.tex)

# Aux files
define  AUXFILE_names
$(addsuffix .$(1),$(patsubst %.tex,%,$(TEXFILES)))
endef
SUFFIXES	= aux log lof out bbl blg toc nav snm vrb bibtex.log
AUXFILES	= $(foreach suffix,$(SUFFIXES),$(call AUXFILE_names,$(suffix)))

# Generated files
GENERATED	= $(XFIGPDFS) $(XFIGPDFTS) $(DOTPDFS) $(DIAEPSS) $(DIAPDFS) $(GNUPLOTPDFS) $(INKSCAPEPDFS)

.PRECIOUS: $(GENERATED)

define HELP_message
make (help)			print this help
make foo (foo.pdf)		build the foo.pdf document
make all			build all documents
make clean			delete non-essential generated files and logs
make ultraclean			delete all generated files, logs, etc.

Buildable documents:
  $(sort $(TARGETS))
Please report bugs or suggestions of improvements to:
  Renaud Pacalet <renaud.pacalet@telecom-paristech.fr>
endef
export HELP_message

define MAXPASSES_message
Sorry, $(MAXPASSES) passes were not sufficient to build the document. Please
check the log files or increase the number of passes:
  make MAXPASSES=5 foo
endef
export MAXPASSES_message

# Targets
help:
	@echo "$$HELP_message"

debug: 
	echo $(PDFTARGETS)

all: $(PDFTARGETS)

$(TARGETS): %: %.pdf

##################
# Xfig to PDFTEX #
##################

define FIG2DEVNOTFOUND_message
Could not find fig2dev. Cannot produce the PDF from fig sources. Please install
fig2dev and point your PATH to the fig2dev executable. Alternately you can also
pass the fig2dev variable wen invoking make:
  make FIG2DEV=/opt/bin/fig2dev foo
endef
export FIG2DEVNOTFOUND_message

# Xfig to PDF (figure without text)
$(XFIGPDFS): %.pdf: %.fig
ifeq ($(FIG2DEV),)
	@echo "$$FIG2DEVNOTFOUND_message"
else
	@echo " [FIG2DEV] $< -> $@"; \
	$(FIG2DEV) $(FIG2DEVFLAGS) -L pdftex $< $@ >> $(GENERATEDLOG) 2>&1
endif

# Xfig to LaTeX (text and included PDF)
$(XFIGPDFTS): %.pdf_t: %.fig %.pdf
ifeq ($(FIG2DEV),)
	@echo "$$FIG2DEVNOTFOUND_message"
else
	@echo " [FIG2DEV] $< -> $@"; \
	$(FIG2DEV) $(FIG2DEVFLAGS) -L pdftex_t -p $(patsubst %.pdf_t,%.pdf,$@) $< $@ >> $(GENERATEDLOG) 2>&1
endif

########################
# dot (graphviz to PDF #
########################

define DOTNOTFOUND_message
Could not find dot. Cannot produce the PDF from svg sources. Please install dot
and point your PATH to the dot executable. Alternately you can also pass the dot
variable wen invoking make:
  make DOT=/opt/bin/dot foo
endef
export DOTNOTFOUND_message

$(DOTPDFS): %.pdf: %.dot
ifeq ($(DOT),)
	@echo "$$DOTNOTFOUND_message"
else
	@echo " [DOT] $< -> $@"; \
	$(DOT) $(DOTFLAGS) -o$@ $< >> $(GENERATEDLOG) 2>&1
endif

########################
# dia to EPS, then PDF #
########################

define DIANOTFOUND_message
Could not find dia. Cannot produce the PDF from svg sources. Please install dia
and point your PATH to the dia executable. Alternately you can also pass the dia
variable wen invoking make:
  make DIA=/opt/bin/dia foo
endef
export DIANOTFOUND_message

# dia to EPS
$(DIAEPSS): %.eps: %.dia
ifeq ($(DIA),)
	@echo "$$DIANOTFOUND_message"
else
	@echo " [DIA] $< -> $@"; \
	$(DIA) $(DIAFLAGS) -e $@ $< >> $(GENERATEDLOG) 2>&1
endif

#########################################
# EPS to PDF (used also for dia to PDF) #
#########################################

define EPSTOPDFNOTFOUND_message
Could not find epstopdf. Cannot produce the PDF from svg sources. Please install epstopdf
and point your PATH to the epstopdf executable. Alternately you can also pass the epstopdf
variable wen invoking make:
  make EPSTOPDF=/opt/bin/epstopdf foo
endef
export EPSTOPDFNOTFOUND_message

$(DIAPDFS) $(EPSPDFS): %.pdf: %.eps
ifeq ($(EPSTOPDF),)
	@echo "$$EPSTOPDFNOTFOUND_message"
else
	@echo " [EPSTOPDF] $< -> $@"; \
	$(EPSTOPDF) $(EPSTOPDFFLAGS) --outfile=$@ $< >> $(GENERATEDLOG) 2>&1
endif

##################
# gnuplot to PDF #
##################

define GNUPLOTNOTFOUND_message
Could not find gnuplot. Cannot produce the PDF from SVG sources. Please install
gnuplot and point your PATH to the gnuplot executable. Alternately you can
also pass the GNUPLOT variable wen invoking make:
  make GNUPLOT=/opt/bin/gnuplot foo
endef
export GNUPLOTNOTFOUND_message

$(GNUPLOTPDFS): %.pdf: %.gnuplot
ifeq ($(GNUPLOT),)
	@echo "$$GNUPLOTNOTFOUND_message"
else
	@echo " [GNUPLOT] $< -> $@"; \
	$(GNUPLOT) $(GNUPLOTFLAGS) $< >> $(GENERATEDLOG) 2>&1
endif

#########################
# inkscape (SVG) to PDF #
#########################

define INKSCAPENOTFOUND_message
Could not find inkscape. Cannot produce the PDF from SVG sources. Please install
inkscape and point your PATH to the inkscape executable. Alternately you can
also pass the INKSCAPE variable wen invoking make:
  make INKSCAPE=/opt/bin/inkscape foo
endef
export INKSCAPENOTFOUND_message

$(INKSCAPEPDFS): %.pdf: %.svg
ifeq ($(INKSCAPE),)
	@echo "$$INKSCAPENOTFOUND_message"
else
	@echo " [INKSCAPE] $< -> $@"; \
	$(INKSCAPE) $(INKSCAPEFLAGS) $< --export-pdf=$@ >> $(GENERATEDLOG) 2>&1
endif

clean:
	@rm -f $(GENERATED) $(AUXFILES) $(GENERATEDLOG)

$(PDFTARGETS): %.pdf: %.tex
	@echo " [PDFLATEX] $< -> $@"; \
	f=$(patsubst %.pdf,%,$@); \
	$(PDFLATEX) $(PDFLATEXFLAGS) $$f > $$f.previous.log 2>&1; \
	if [ -f $$f.aux ]; then \
		if egrep -q '(\\citation)|(\\bibdata)|(\\bibstyle)' $$f.aux; then \
			echo "  [BIBTEX] $$f"; \
			$(BIBTEX) $$f > $$f.bibtex.log 2>&1; \
		fi; \
	fi; \
	for (( i = 1; i <= $(MAXPASSES); i += 1 )); do \
		echo "  [PASS #$$i]"; \
		$(PDFLATEX) $(PDFLATEXFLAGS) $$f > $$f.log 2>&1; \
		if diff --brief $$f.log $$f.previous.log &> /dev/null; then \
			break; \
		fi; \
		mv $$f.log $$f.previous.log; \
	done; \
	if (( i > $(MAXPASSES) )); then \
		echo "$$MAXPASSES_message"; \
		echo "Keeping last two log messages: $$f.previous.log and $$f.log"; \
		echo "comparing them may help understanding the problem."; \
		exit -1; \
	fi; \
	rm $$f.previous.log; \
	grep -v 'Package: infwarerr .* Providing info/warning/error messages' $$f.log | \
		grep -iE 'warning|error' || rm $$f.log; \
	if [ -f $(GENERATEDLOG) ]; then \
		grep -iE 'warning|error' $(GENERATEDLOG) || rm $(GENERATEDLOG); \
	fi

ultraclean: clean
	@rm -f $(PDFTARGETS)
