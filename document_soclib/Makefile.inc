SHELL 		= /bin/bash
TEXTOPS		= $(shell grep -l '[^%]*\\begin{document}' *.tex)
PDFTARGETS	= $(patsubst %.tex,%.pdf,$(TEXTOPS))
TARGETS		= $(patsubst %.tex,%,$(TEXTOPS))

# Tools
PDFLATEX	= pdflatex
#PDFLATEXFLAGS	= -halt-on-error -interaction=batchmode
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
DOTPDFS		= $(patsubst %.dot,%.pdf_dot,$(DOTSRCS))
DIASRCS		= $(wildcard $(FIGDIR)/*.dia)
DIAEPSS		= $(patsubst %.dia,%.eps,$(DIASRCS))
DIAPDFS		= $(patsubst %.dia,%.pdf,$(DIASRCS))
INKSCAPESRCS	= $(wildcard $(FIGDIR)/*.svg)
INKSCAPEPDFS	= $(patsubst %.svg,%.pdf,$(INKSCAPESRCS))
GNUPLOTSRCS	= $(wildcard $(FIGDIR)/*.gnuplot)
GNUPLOTPDFS	= $(patsubst %.gnuplot,%.pdf,$(GNUPLOTSRCS))

# Tex files
TEXFILES	= $(wildcard *.tex)

# Generated files
#GENERATED	= $(XFIGPDFS) $(XFIGPDFTS) $(DOTPDFS) $(DIAPDFS) $(GNUPLOTPDFS) $(INKSCAPEPDFS)
GENERATED	= $(XFIGPDFS) $(XFIGPDFTS) $(DOTPDFS) $(DIAPDFS) $(GNUPLOTPDFS)

.PRECIOUS: $(GENERATED)

define HELP_message
--------------------------------------------------------------------------------
available targets:
  "make" or "make help":        print this help
  "make foo" or "make foo.pdf": build the foo.pdf document
  "make all":                   build all documents
  "make clean":                 delete non-essential generated files
  "make ultraclean":            delete all generated files
--------------------------------------------------------------------------------
Buildable documents:
  $(sort $(TARGETS))
if you encounter problems please contact:
  Renaud Pacalet <renaud.pacalet@telecom-paristech.fr>
--------------------------------------------------------------------------------
endef
export HELP_message

define MAXPASSES_message
--------------------------------------------------------------------------------
Sorry, $(MAXPASSES) passes were not sufficient to build the document. Please
check the log files or increase the number of passes:
  "make MAXPASSES=5 foo"
--------------------------------------------------------------------------------
endef
export MAXPASSES_message

define FIG2DEVNOTFOUND_message
--------------------------------------------------------------------------------
Could not find fig2dev. Cannot produce the PDF from fig sources. Please install
fig2dev and point your PATH to the fig2dev executable. Alternately you can also
pass the fig2dev variable wen invoking make:
  "make FIG2DEV=/opt/bin/fig2dev foo"
--------------------------------------------------------------------------------
endef
export FIG2DEVNOTFOUND_message

define DIANOTFOUND_message
--------------------------------------------------------------------------------
Could not find dia. Cannot produce the PDF from svg sources. Please install dia
and point your PATH to the dia executable. Alternately you can also pass the dia
variable wen invoking make:
  "make DIA=/opt/bin/dia foo"
--------------------------------------------------------------------------------
endef
export DIANOTFOUND_message

define EPSTOPDFNOTFOUND_message
--------------------------------------------------------------------------------
Could not find epstopdf. Cannot produce the PDF from svg sources. Please install epstopdf
and point your PATH to the epstopdf executable. Alternately you can also pass the epstopdf
variable wen invoking make:
  "make EPSTOPDF=/opt/bin/epstopdf foo"
--------------------------------------------------------------------------------
endef
export EPSTOPDFNOTFOUND_message

define DOTNOTFOUND_message
--------------------------------------------------------------------------------
Could not find dot. Cannot produce the PDF from svg sources. Please install dot
and point your PATH to the dot executable. Alternately you can also pass the dot
variable wen invoking make:
  "make DOT=/opt/bin/dot foo"
--------------------------------------------------------------------------------
endef
export DOTNOTFOUND_message

define GNUPLOTNOTFOUND_message
--------------------------------------------------------------------------------
Could not find gnuplot. Cannot produce the PDF from SVG sources. Please install
gnuplot and point your PATH to the gnuplot executable. Alternately you can
also pass the GNUPLOT variable wen invoking make:
  "make GNUPLOT=/opt/bin/gnuplot foo"
--------------------------------------------------------------------------------
endef
export GNUPLOTNOTFOUND_message

define INKSCAPENOTFOUND_message
--------------------------------------------------------------------------------
Could not find inkscape. Cannot produce the PDF from SVG sources. Please install
inkscape and point your PATH to the inkscape executable. Alternately you can
also pass the INKSCAPE variable wen invoking make:
  "make INKSCAPE=/opt/bin/inkscape foo"
--------------------------------------------------------------------------------
endef
export INKSCAPENOTFOUND_message

# Targets
help:
	@echo "$$HELP_message"

debug: 
	echo $(PDFTARGETS)

all: $(PDFTARGETS)

$(TARGETS): %: %.pdf

$(XFIGPDFS): %.pdf: %.fig
ifeq ($(FIG2DEV),)
	@echo "$$FIG2DEVNOTFOUND_message"
else
	@echo " [FIG2DEV] $< -> $@"; \
	$(FIG2DEV) $(FIG2DEVFLAGS) -L pdf $< $@
endif

$(XFIGPDFTS): %.pdf_t: %.fig %.pdf
ifeq ($(FIG2DEV),)
	@echo "$$FIG2DEVNOTFOUND_message"
else
	@echo " [FIG2DEV] $< -> $@"; \
	$(FIG2DEV) $(FIG2DEVFLAGS) -L pdftex_t -p $(patsubst %.pdf_t,%.pdf,$@) $< $@
endif

$(DOTPDFS): %.pdf: %.dot
ifeq ($(DOT),)
	@echo "$$DOTNOTFOUND_message"
else
	@echo " [DOT] $< -> $@"; \
	$(DOT) $(DOTFLAGS) -o$@ $< > $@.log
endif

$(DIAEPSS): %.eps: %.dia
ifeq ($(DIA),)
	@echo "$$DIANOTFOUND_message"
else
	@echo " [DIA] $< -> $@"; \
	$(DIA) $(DIAFLAGS) -e $@ $<
endif

$(DIAPDFS): %.pdf: $(FIGDIR)/%.eps
ifeq ($(EPSTOPDF),)
	@echo "$$EPSTOPDFNOTFOUND_message"
else
	@echo " [EPSTOPDF] $< -> $@"; \
	$(EPSTOPDF) $(EPSTOPDFFLAGS) --outfile=$@ $<
endif

$(GNUPLOTPDFS): %.pdf: %.gnuplot
ifeq ($(GNUPLOT),)
	@echo "$$GNUPLOTNOTFOUND_message"
else
	@echo " [GNUPLOT] $< -> $@"; \
	cd $(FIGDIR); $(GNUPLOT) $(GNUPLOTFLAGS) $< > $(notdir $@)
endif

$(INKSCAPEPDFS): %.pdf: %.svg
ifeq ($(INKSCAPE),)
	@echo "$$INKSCAPENOTFOUND_message"
else
	@echo " [INKSCAPE] $< -> $@"; \
	$(INKSCAPE) $(INKSCAPEFLAGS) $< --export-pdf=$@
endif


clean:
	@rm -f $(GENERATED) *.aux *.log *.out *.bbl *.blg *.toc *.nav *.snm

$(PDFTARGETS): %.pdf: %.tex
	@echo " [PDFLATEX] $< -> $@"; \
	f=$(patsubst %.pdf,%,$@); \
	$(PDFLATEX) $(PDFLATEXFLAGS) $$f; \
	cp $$f.log /tmp/$$f.log; \
	$(BIBTEX) $$f; \
	for (( i = 1; i <= $(MAXPASSES); i += 1 )); do \
		echo "[PASS #$$i]"; \
		$(PDFLATEX) $(PDFLATEXFLAGS) $$f; \
		if diff --brief $$f.log /tmp/$$f.log &> /dev/null; then \
			break; \
		fi; \
		cp $$f.log /tmp/$$f.log; \
	done; \
	if (( i > $(MAXPASSES) )); then \
		echo "$$MAXPASSES_message"; \
		exit -1; \
	fi; \
	grep -iE 'warning|error' $$f.log || rm $$f.log

ultraclean: clean
	@rm -f $(PDFTARGETS)
