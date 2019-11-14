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
# List of figures source directories
FIGDIR		?= fig
# Build directory
BUILDDIR	?= build
# Verbosity (0: min...)
V		?= 0
# Tools
# LaTeX compilation
LATEX		?= $(shell which pdflatex 2> /dev/null)
LATEXFLAGS	?= -shell-escape -halt-on-error -interaction=batchmode -output-directory=$(BUILDDIR)
# BibTex
BIBTEX		?= $(shell which bibtex 2> /dev/null)
BIBTEXFLAGS	?=
# Xfig to LaTeX/PDF
FIG2DEV		?= $(shell which fig2dev 2> /dev/null)
FIG2DEVFLAGS	?=
# Graphviz to PDF
DOT		?= $(shell which dot 2> /dev/null)
DOTFLAGS	?= -Tpdf
# EPS to PDF
EPSTOPDF	?= $(shell which epstopdf 2> /dev/null)
EPSTOPDFFLAGS	?=
# DIA to EPS
DIA		?= $(shell which dia 2> /dev/null)
DIAFLAGS	?= -t eps-pango
# Gnuplot to PDF
GNUPLOT		?= $(shell which gnuplot 2> /dev/null)
GNUPLOTFLAGS	?=
# SVG to PDF
SVGTOPDF	?= $(shell which inkscape 2> /dev/null)
SVGTOPDFFLAGS	?=

#########################################################
# Normally you shouldn't have to change anything below. #
# Unless you know what you are doing, of course.        #
#########################################################

# Verbosity
MAXV			:= 2

ifeq ($(V),0)

Q	:= @
ECHO	:= @echo
P	:= &> /dev/null

else ifeq ($(V),1)

Q	:=
ECHO	:= @echo -e '\n************************************************************************' && echo
P	:= &> /dev/null

else

Q	:=
ECHO	:= @echo -e '\n************************************************************************' && echo
P	:=

endif

# Files, path...
TEXTOPS		= $(shell grep -l '[^%]*\\begin{document}' *.tex)
PDFTARGETS	= $(patsubst %.tex,$(BUILDDIR)/%.pdf,$(TEXTOPS))
TARGETS		= $(patsubst %.tex,%,$(TEXTOPS))
TEXFILES	= $(wildcard *.tex)

# Aux files
TO_STOP_SUFFIXES	= aux bcf fls idx ind lof lot out toc
TO_CLEAN_SUFFIXES	= $(TO_STOP_SUFFIXES) log bbl blg nav snm vrb
TO_CLEAN_FILES		= $(foreach t,$(TARGETS),$(foreach s,$(TO_CLEAN_SUFFIXES),$(BUILDDIR)/$(t).$(s) $(BUILDDIR)/$(t).$(s).1))

# Figures build files
FIGURES	= $(XFIGPDFS) $(XFIGPDFTS) $(DOTPDFS) $(EPSPDFS) $(DIAEPSS) $(DIAPDFS) $(GNUPLOTPDFS) $(SVGPDFS)

.PRECIOUS: $(FIGURES)

define HELP_message
Main goals:
  help					print this help (default goal)
  foo (or BUILDDIR/foo.pdf)		build the BUILDDIR/foo.pdf document
  all					build all documents
  clean					delete generated files except PDFs of documents
  ultraclean				delete all generated files (but not the build directory)

Main make variables (current value):
  SHELL					must be bash ($(SHELL))
  MAXPASSES				max number of compilations ($(MAXPASSES))
  FIGDIR				figures source directory ($(FIGDIR))
  BUILDDIR				build directory ($(BUILDDIR))
  V					verbosity - 0...$(MAXV) ($(V))
Please see mli.mk for other customizable variables.

Buildable documents:
  $(sort $(TARGETS))

Please report bugs or suggestions of improvements to:
  Renaud Pacalet <renaud.pacalet@telecom-paristech.fr>
endef
export HELP_message

define MAXPASSES_message
Sorry, $(MAXPASSES) passes were not sufficient to build the document. Keeping
two last versions of each generated files. Comparing them may help
understanding the problem:
  diff $(BUILDDIR)/foo.aux $(BUILDDIR)/foo.aux.1
Alternately, you can also try to increase the maximum number of passes:
  make MAXPASSES=5 foo
or the verbosity level:
  make V=0...$(MAXV) foo
endef
export MAXPASSES_message

# Targets
help:
	@echo "$$HELP_message"

debug: 
	echo $(PDFTARGETS)

all: $(PDFTARGETS)

$(TARGETS): %: $(BUILDDIR)/%.pdf

$(PDFTARGETS) $(FIGURES): | $(BUILDDIR)

##################
# xfig to PDFTEX #
##################

define FIG2DEVNOTFOUND_message
Could not find fig2dev. Cannot produce the pdf from fig sources. Please install
fig2dev and point your PATH to the fig2dev executable. Alternately you can also
pass the fig2dev variable wen invoking make:
  make FIG2DEV=/opt/bin/fig2dev foo
endef
export FIG2DEVNOTFOUND_message

# xfig to PDF (figure without text) and LaTeX (text and included PDF)
define XFIGTOPDF_rule

$(BUILDDIR)/$(2)-fig.pdf: $(1)/$(2).fig
ifeq ($(FIG2DEV),)
	@echo "$$FIG2DEVNOTFOUND_message"
else
	$(ECHO) "[FIG2DEV]	$$< -> $$@"
	$(Q)$(FIG2DEV) $(FIG2DEVFLAGS) -L pdftex $$< $$@ $(P)
endif

$(BUILDDIR)/$(2)-fig.pdf_t: $(1)/$(2).fig $(BUILDDIR)/$(2)-fig.pdf
ifeq ($(FIG2DEV),)
	@echo "$$FIG2DEVNOTFOUND_message"
else
	$(ECHO) "[FIG2DEV]	$$< -> $$@"
	$(Q)$(FIG2DEV) $(FIG2DEVFLAGS) -L pdftex_t -p $(BUILDDIR)/$(2)-fig.pdf $$< $$@ $(P)
endif

XFIGPDFS	+= $(BUILDDIR)/$(2)-fig.pdf
XFIGPDFTS	+= $(BUILDDIR)/$(2)-fig.pdf_t

endef

$(foreach d,$(FIGDIR),$(foreach f,$(patsubst $(d)/%.fig,%,$(wildcard $(d)/*.fig)),$(eval $(call XFIGTOPDF_rule,$(d),$(f)))))

#########################
# dot (graphviz) to PDF #
#########################

define DOTNOTFOUND_message
Could not find dot. Cannot produce the PDF from dot sources. Please install dot
and point your PATH to the dot executable. Alternately you can also pass the dot
variable wen invoking make:
  make DOT=/opt/bin/dot foo
endef
export DOTNOTFOUND_message

define DOTTOPDF_rule

$(BUILDDIR)/$(2)-dot.pdf: $(1)/$(2).dot
ifeq ($(DOT),)
	@echo "$$DOTNOTFOUND_message"
else
	$(ECHO) '[DOT]		$$< -> $$@'
	$(Q)$(DOT) $(DOTFLAGS) -o$$@ $$< $(P)
endif

DOTPDFS	+= $(BUILDDIR)/$(2)-dot.pdf

endef

$(foreach d,$(FIGDIR),$(foreach f,$(patsubst $(d)/%.dot,%,$(wildcard $(d)/*.dot)),$(eval $(call DOTTOPDF_rule,$(d),$(f)))))

##############
# EPS to PDF #
##############

define EPSTOPDFNOTFOUND_message
Could not find epstopdf. Cannot produce the PDF from eps sources. Please install epstopdf
and point your PATH to the epstopdf executable. Alternately you can also pass the epstopdf
variable wen invoking make:
  make EPSTOPDF=/opt/bin/epstopdf foo
endef
export EPSTOPDFNOTFOUND_message

define EPSTOPDF_rule

$(BUILDDIR)/$(2).eps.pdf: $(1)/$(2).eps
ifeq ($(EPSTOPDF),)
	@echo "$$EPSTOPDFNOTFOUND_message"
else
	$(ECHO) '[EPSTOPDF]	$$< -> $$@'
	$(Q)$(EPSTOPDF) $(EPSTOPDFFLAGS) --outfile=$$@ $$< $(P)
endif

EPSPDFS	+= $(BUILDDIR)/$(2)-eps.pdf

endef

$(foreach d,$(FIGDIR),$(foreach f,$(patsubst $(d)/%.eps,%,$(filter-out $(DIAEPSS),$(wildcard $(d)/*.eps))),$(eval $(call EPSTOPDF_rule,$(d),$(f)))))

######################
# dia to EPS and PDF #
######################

define DIANOTFOUND_message
Could not find dia. Cannot produce the PDF from dia sources. Please install dia
and point your PATH to the dia executable. Alternately you can also pass the dia
variable wen invoking make:
  make DIA=/opt/bin/dia foo
endef
export DIANOTFOUND_message

# dia to EPS and then to PDF
define DIATOPDF_rule

$(BUILDDIR)/$(2)-dia.eps: $(1)/$(2).dia
ifeq ($(DIA),)
	@echo "$$DIANOTFOUND_message"
else
	$(ECHO) '[DIA]		$$< -> $$@'
	$(Q)$(DIA) $(DIAFLAGS) -e $$@ $$< $(P)
endif

$(BUILDDIR)/$(2)-dia.pdf: $(BUILDDIR)/$(2)-dia.eps
ifeq ($(EPSTOPDF),)
	@echo "$$EPSTOPDFNOTFOUND_message"
else
	$(ECHO) '[DIAEPSTOPDF]	$$< -> $$@'
	$(Q)$(EPSTOPDF) $(EPSTOPDFFLAGS) --outfile=$$@ $$< $(P)
endif

DIAEPSS	+= $(BUILDDIR)/$(2)-dia.eps
DIAPDFS	+= $(BUILDDIR)/$(2)-dia.pdf

endef

$(foreach d,$(FIGDIR),$(foreach f,$(patsubst $(d)/%.dia,%,$(wildcard $(d)/*.dia)),$(eval $(call DIATOPDF_rule,$(d),$(f)))))

##################
# gnuplot to PDF #
##################

define GNUPLOTNOTFOUND_message
Could not find gnuplot. Cannot produce the PDF from gnuplot sources. Please install
gnuplot and point your PATH to the gnuplot executable. Alternately you can
also pass the GNUPLOT variable wen invoking make:
  make GNUPLOT=/opt/bin/gnuplot foo
endef
export GNUPLOTNOTFOUND_message

define GNUPLOTTOPDF_rule

$(BUILDDIR)/$(2)-gnuplot.pdf: $(1)/$(2).gnuplot
ifeq ($(GNUPLOT),)
	@echo "$$GNUPLOTNOTFOUND_message"
else
	$(ECHO) '[GNUPLOT]	$$< -> $$@'
	$(Q)$(GNUPLOT) $(GNUPLOTFLAGS) -e "set output '| ps2pdf - $$@'" $$< $(P)
endif

GNUPLOTPDFS	+= $(BUILDDIR)/$(2)-gnuplot.pdf

endef

$(foreach d,$(FIGDIR),$(foreach f,$(patsubst $(d)/%.gnuplot,%,$(wildcard $(d)/*.gnuplot)),$(eval $(call GNUPLOTTOPDF_rule,$(d),$(f)))))

##############
# SVG to PDF #
##############

define SVGTOPDFNOTFOUND_message
Could not find $(SVGTOPDF). Cannot produce the PDF from svg sources. Please install
$(SVGTOPDF) and point your PATH to the $(SVGTOPDF) executable. Alternately you can
also pass the SVGTOPDF variable wen invoking make:
  make SVGTOPDF=/opt/bin/inkscape foo
endef
export SVGTOPDFNOTFOUND_message

define SVGTOPDF_rule

$(BUILDDIR)/$(2)-svg.pdf: $(1)/$(2).svg
ifeq ($(SVGTOPDF),)
	@echo "$$SVGTOPDFNOTFOUND_message"
else
	$(ECHO) '[SVGTOPDF]	$$< -> $$@'
	$(Q)$(SVGTOPDF) $(SVGTOPDFFLAGS) $$< --export-pdf=$$@ $(P)
endif

SVGPDFS	+= $(BUILDDIR)/$(2)-svg.pdf

endef

$(foreach d,$(FIGDIR),$(foreach f,$(patsubst $(d)/%.svg,%,$(wildcard $(d)/*.svg)),$(eval $(call SVGTOPDF_rule,$(d),$(f)))))

define LATEX_run
.PHONY: $(1)_latex

$(1)_latex:
	$(ECHO) '[LATEX #$$(N)]	$(1).tex -> $(BUILDDIR)/$(1).pdf'
	$(Q)$(LATEX) $(LATEXFLAGS) $(1) $(P)
endef
$(foreach doc,$(TARGETS),$(eval $(call LATEX_run,$(doc))))

define BIBTEX_run
.PHONY: $(1)_bibtex

$(1)_bibtex:
	$(ECHO) '[BIBTEX]	$(BUILDDIR)/$(1).aux -> $(BUILDDIR)/$(1).bbl'
	-$(Q)$(BIBTEX) $(BUILDDIR)/$(1) $(P)
endef
$(foreach doc,$(TARGETS),$(eval $(call BIBTEX_run,$(doc))))

$(PDFTARGETS): $(BUILDDIR)/%.pdf: %.tex
	@f=$(patsubst %.tex,%,$<) && \
	for (( i = 1; i <= $(MAXPASSES); i += 1 )); do \
		stop=1 && \
		for e in $(TO_STOP_SUFFIXES); do \
			if [ -f $(BUILDDIR)/$$f.$$e ]; then \
				cp -f $(BUILDDIR)/$$f.$$e $(BUILDDIR)/$$f.$$e.1; \
			fi; \
		done && \
		$(MAKE) --no-print-directory N=$$i $*_latex && \
		if [ $$i -eq 1 -a -f $(BUILDDIR)/$$f.aux ] && egrep -q '(\\citation)|(\\bibdata)|(\\bibstyle)' $(BUILDDIR)/$$f.aux; then \
			stop=0 && \
			$(MAKE) --no-print-directory $*_bibtex; \
		fi && \
		for e in $(TO_STOP_SUFFIXES); do \
			if [ -f $(BUILDDIR)/$$f.$$e ]; then \
				if [ ! -f $(BUILDDIR)/$$f.$$e.1 ]; then \
					stop=0; \
				elif ! diff --brief $(BUILDDIR)/$$f.$$e $(BUILDDIR)/$$f.$$e.1 &> /dev/null; then \
					stop=0; \
				fi; \
			fi; \
		done && \
		if [ $$stop -eq 1 ]; then \
			break; \
		fi && \
		if [ $$i -lt $(MAXPASSES) ]; then \
			for e in $(TO_STOP_SUFFIXES); do \
				if [ -f $(BUILDDIR)/$$f.$$e ]; then \
					cp -f $(BUILDDIR)/$$f.$$e $(BUILDDIR)/$$f.$$e.1; \
				fi; \
			done; \
		fi; \
	done && \
	if [ $$i -gt $(MAXPASSES) ]; then \
		echo "$$MAXPASSES_message" && \
		exit -1; \
	fi && \
	rm -f $(BUILDDIR)/*.1

$(BUILDDIR):
	$(ECHO) '[MKDIR]		$(BUILDDIR)'
	$(Q)mkdir -p $(BUILDDIR)

clean:
	$(ECHO) '[RM]		figures and temporary files'
	$(Q)rm -f $(FIGURES) $(TO_CLEAN_FILES)

ultraclean: clean
	$(ECHO) '[RM]		$(PDFTARGETS)'
	$(Q)rm -f $(PDFTARGETS)
