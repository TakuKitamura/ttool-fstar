###########################################################
#                 COPYRIGHT                               #
#                 ---------                               #
#                                                         #
# See Copyright Notice in COPYING and license in LICENSE. #
###########################################################

###################################################
# Default values of most important make variables #
###################################################

# mli needs bash
SHELL		?= /bin/bash
# Default max number of compilations
MAXPASSES	?= 30
# Default sub-directory of sources of figures
FIGDIR		?= figures
# Default bibliography
BIBLIO		?= $(wildcard biblio.bib)
# Build directory
BUILDDIR	?= build

#########################################################
# Normally you shouldn't have to change anything below. #
# Unless you know what you are doing, of course.        #
#########################################################

# Tools
PDFLATEX	?= $(shell which pdflatex 2> /dev/null)
PDFLATEXFLAGS	?= -halt-on-error -interaction=batchmode -output-directory=$(BUILDDIR)
BIBTEX		?= $(shell which bibtex 2> /dev/null)
BIBTEXFLAGS	?=
FIG2DEV		?= $(shell which fig2dev 2> /dev/null)
FIG2DEVFLAGS	?=
DOT		?= $(shell which dot 2> /dev/null)
DOTFLAGS	?= -Tpdf
EPSTOPDF	?= $(shell which epstopdf 2> /dev/null)
EPSTOPDFFLAGS	?=
DIA		?= $(shell which dia 2> /dev/null)
DIAFLAGS	?= -t eps-pango
GNUPLOT		?= $(shell which gnuplot 2> /dev/null)
GNUPLOTFLAGS	?=
INKSCAPE	?= $(shell which inkscape 2> /dev/null)
INKSCAPEFLAGS	?=

# Files, path...
TEXTOPS		= $(shell grep -l '[^%]*\\begin{document}' *.tex)
PDFTARGETS	= $(patsubst %.tex,$(BUILDDIR)/%.pdf,$(TEXTOPS))
TARGETS		= $(patsubst %.tex,%,$(TEXTOPS))
FIGURESLOG	= $(BUILDDIR)/figures.log
XFIGSRCS	= $(wildcard $(FIGDIR)/*.fig)
XFIGPDFS	= $(patsubst $(FIGDIR)/%.fig,$(BUILDDIR)/%-fig.pdf,$(XFIGSRCS))
XFIGPDFTS	= $(patsubst $(FIGDIR)/%.fig,$(BUILDDIR)/%-fig.pdf_t,$(XFIGSRCS))
DOTSRCS		= $(wildcard $(FIGDIR)/*.dot)
DOTPDFS		= $(patsubst $(FIGDIR)/%.dot,$(BUILDDIR)/%-dot.pdf,$(DOTSRCS))
EPSSRCS		= $(filter-out $(DIAEPSS),$(wildcard $(FIGDIR)/*.eps))
EPSPDFS		= $(patsubst $(FIGDIR)/%.eps,$(BUILDDIR)/%-eps.pdf,$(EPSSRCS))
DIASRCS		= $(wildcard $(FIGDIR)/*.dia)
DIAEPSS		= $(patsubst $(FIGDIR)/%.dia,$(BUILDDIR)/%-dia.eps,$(DIASRCS))
DIAPDFS		= $(patsubst $(FIGDIR)/%.dia,$(BUILDDIR)/%-dia.pdf,$(DIASRCS))
GNUPLOTSRCS	= $(wildcard $(FIGDIR)/*.gnuplot)
GNUPLOTPDFS	= $(patsubst $(FIGDIR)/%.gnuplot,$(BUILDDIR)/%-gnuplot.pdf,$(GNUPLOTSRCS))
SVGSRCS		= $(wildcard $(FIGDIR)/*.svg)
SVGPDFS		= $(patsubst $(FIGDIR)/%.svg,$(BUILDDIR)/%-svg.pdf,$(SVGSRCS))

# Tex files
TEXFILES	= $(wildcard *.tex)

# Aux files
define  AUXFILE_names
$(addsuffix .$(1),$(patsubst %.tex,%,$(TEXFILES)))
endef
SUFFIXES	= aux log previous.log lof out bbl blg toc nav snm vrb bibtex.log
AUXFILES	= $(addprefix $(BUILDDIR)/,$(foreach suffix,$(SUFFIXES),$(call AUXFILE_names,$(suffix))))

# Figures build files
FIGURES	= $(XFIGPDFS) $(XFIGPDFTS) $(DOTPDFS) $(EPSPDFS) $(DIAEPSS) $(DIAPDFS) $(GNUPLOTPDFS) $(SVGPDFS)

.PRECIOUS: $(FIGURES)

define HELP_message
make (help)				print this help
make foo (or $(BUILDDIR)/foo.pdf)	build the $(BUILDDIR)/foo.pdf document
make all				build all documents
make clean				delete generated files except PDFs of documents
make ultraclean				delete all generated files (but not the build directory)

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

$(TARGETS): %: $(BUILDDIR)/%.pdf

$(PDFTARGETS) $(FIGURES): $(BUILDDIR)/.exists

##################
# Xfig to PDFTEX #
##################

define FIG2DEVNOTFOUND_message
Could not find fig2dev. Cannot produce the pdf from fig sources. Please install
fig2dev and point your PATH to the fig2dev executable. Alternately you can also
pass the fig2dev variable wen invoking make:
  make FIG2DEV=/opt/bin/fig2dev foo
endef
export FIG2DEVNOTFOUND_message

# Xfig to PDF (figure without text)
$(XFIGPDFS): $(BUILDDIR)/%-fig.pdf: $(FIGDIR)/%.fig
ifeq ($(FIG2DEV),)
	@echo "$$FIG2DEVNOTFOUND_message"
else
	@echo " [FIG2DEV] $< -> $@"; \
	$(FIG2DEV) $(FIG2DEVFLAGS) -L pdftex $< $@ >> $(FIGURESLOG) 2>&1
endif

# Xfig to LaTeX (text and included PDF)
$(XFIGPDFTS): $(BUILDDIR)/%-fig.pdf_t: $(FIGDIR)/%.fig $(BUILDDIR)/%-fig.pdf
ifeq ($(FIG2DEV),)
	@echo "$$FIG2DEVNOTFOUND_message"
else
	@echo " [FIG2DEV] $< -> $@"; \
	$(FIG2DEV) $(FIG2DEVFLAGS) -L pdftex_t -p $(patsubst %.pdf_t,%.pdf,$@) $< $@ >> $(FIGURESLOG) 2>&1
endif

########################
# dot (graphviz to PDF #
########################

define DOTNOTFOUND_message
Could not find dot. Cannot produce the pdf from dot sources. Please install dot
and point your PATH to the dot executable. Alternately you can also pass the dot
variable wen invoking make:
  make DOT=/opt/bin/dot foo
endef
export DOTNOTFOUND_message

$(DOTPDFS): $(BUILDDIR)/%-dot.pdf: $(FIGDIR)/%.dot
ifeq ($(DOT),)
	@echo "$$DOTNOTFOUND_message"
else
	@echo " [DOT] $< -> $@"; \
	$(DOT) $(DOTFLAGS) -o$@ $< >> $(FIGURESLOG) 2>&1
endif

##############
# EPS to PDF #
##############

define EPSTOPDFNOTFOUND_message
Could not find epstopdf. Cannot produce the pdf from eps sources. Please install epstopdf
and point your PATH to the epstopdf executable. Alternately you can also pass the epstopdf
variable wen invoking make:
  make EPSTOPDF=/opt/bin/epstopdf foo
endef
export EPSTOPDFNOTFOUND_message

$(EPSPDFS): $(BUILDDIR)/%.eps.pdf: $(FIGDIR)/%.eps
ifeq ($(EPSTOPDF),)
	@echo "$$EPSTOPDFNOTFOUND_message"
else
	@echo " [EPSTOPDF] $< -> $@"; \
	$(EPSTOPDF) $(EPSTOPDFFLAGS) --outfile=$@ $< >> $(FIGURESLOG) 2>&1
endif

######################
# dia to EPS and PDF #
######################

define DIANOTFOUND_message
Could not find dia. Cannot produce the pdf from dia sources. Please install dia
and point your PATH to the dia executable. Alternately you can also pass the dia
variable wen invoking make:
  make DIA=/opt/bin/dia foo
endef
export DIANOTFOUND_message

# dia to EPS
$(DIAEPSS): $(BUILDDIR)/%-dia.eps: $(FIGDIR)/%.dia
ifeq ($(DIA),)
	@echo "$$DIANOTFOUND_message"
else
	@echo " [DIA] $< -> $@"; \
	$(DIA) $(DIAFLAGS) -e $@ $< >> $(FIGURESLOG) 2>&1
endif

# dia to PDF
$(DIAPDFS): $(BUILDDIR)/%-dia.pdf: $(BUILDDIR)/%-dia.eps
ifeq ($(EPSTOPDF),)
	@echo "$$EPSTOPDFNOTFOUND_message"
else
	@echo " [DIAEPSTOPDF] $< -> $@"; \
	$(EPSTOPDF) $(EPSTOPDFFLAGS) --outfile=$@ $< >> $(FIGURESLOG) 2>&1
endif

##################
# gnuplot to PDF #
##################

define GNUPLOTNOTFOUND_message
Could not find gnuplot. Cannot produce the pdf from gnuplot sources. Please install
gnuplot and point your PATH to the gnuplot executable. Alternately you can
also pass the GNUPLOT variable wen invoking make:
  make GNUPLOT=/opt/bin/gnuplot foo
endef
export GNUPLOTNOTFOUND_message

$(GNUPLOTPDFS): $(BUILDDIR)/%-gnuplot.pdf: $(FIGDIR)/%.gnuplot
ifeq ($(GNUPLOT),)
	@echo "$$GNUPLOTNOTFOUND_message"
else
	@echo " [GNUPLOT] $< -> $@"; \
	$(GNUPLOT) $(GNUPLOTFLAGS) -e "set output '| ps2pdf - $@'" $< >> $(FIGURESLOG) 2>&1
endif

##############
# SVG to PDF #
##############

define INKSCAPENOTFOUND_message
Could not find inkscape. Cannot produce the pdf from svg sources. Please install
inkscape and point your PATH to the inkscape executable. Alternately you can
also pass the INKSCAPE variable wen invoking make:
  make INKSCAPE=/opt/bin/inkscape foo
endef
export INKSCAPENOTFOUND_message

$(SVGPDFS): $(BUILDDIR)/%-svg.pdf: $(FIGDIR)/%.svg
ifeq ($(INKSCAPE),)
	@echo "$$INKSCAPENOTFOUND_message"
else
	@echo " [INKSCAPE] $< -> $@"; \
	$(INKSCAPE) $(INKSCAPEFLAGS) $< --export-pdf=$@ >> $(FIGURESLOG) 2>&1
endif

$(PDFTARGETS): $(BUILDDIR)/%.pdf: %.tex
	@echo " [PDFLATEX] $< -> $@"; \
	f=$(patsubst %.tex,%,$<); \
	$(PDFLATEX) $(PDFLATEXFLAGS) $$f > $(BUILDDIR)/$$f.previous.log 2>&1; \
	if [ -f $(BUILDDIR)/$$f.aux ]; then \
		if egrep -q '(\\citation)|(\\bibdata)|(\\bibstyle)' $(BUILDDIR)/$$f.aux; then \
			echo "  [BIBTEX] $$f"; \
			$(BIBTEX) $(BUILDDIR)/$$f > $(BUILDDIR)/$$f.bibtex.log 2>&1; \
		fi; \
	fi; \
	for (( i = 1; i <= $(MAXPASSES); i += 1 )); do \
		echo "  [PASS #$$i]"; \
		$(PDFLATEX) $(PDFLATEXFLAGS) $$f > $(BUILDDIR)/$$f.log 2>&1; \
		if diff --brief $(BUILDDIR)/$$f.log $(BUILDDIR)/$$f.previous.log &> /dev/null; then \
			break; \
		fi; \
		if (( i < $(MAXPASSES) )); then \
			mv $(BUILDDIR)/$$f.log $(BUILDDIR)/$$f.previous.log; \
		fi; \
	done; \
	if (( i > $(MAXPASSES) )); then \
		echo "$$MAXPASSES_message"; \
		echo "Keeping last two log messages. Comparing them may help understanding the problem:"; \
		echo "diff $(BUILDDIR)/$$f.previous.log $(BUILDDIR)/$$f.log"; \
		exit -1; \
	fi; \
	rm -f $(BUILDDIR)/$$f.previous.log; \
	grep -v 'Package: infwarerr .* Providing info/warning/error messages' $(BUILDDIR)/$$f.log | \
		grep -iE 'warning|error' || rm -f $(BUILDDIR)/$$f.log; \
	if [ -f $(FIGURESLOG) ]; then \
		grep -iE 'warning|error' $(FIGURESLOG) || rm -f $(FIGURESLOG); \
	fi

$(BUILDDIR)/.exists:
	@mkdir -p $(BUILDDIR); \
	touch $@

clean:
	@rm -f $(FIGURES) $(AUXFILES) $(FIGURESLOG)

ultraclean: clean
	@rm -f $(PDFTARGETS) $(BUILDDIR)/.exists
