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
SHELL		= bash
# Default max number of compilations
MAXPASSES	?= 30
# List of figures source directories
FIGDIR		?= figures
# Build directory
BUILDDIR	?= build
# Verbosity (0: min...)
V		?= 0
# Tools
# LaTeX compilation
LATEX		?= $(shell which pdflatex 2> /dev/null)
LATEXFLAGS	?= -halt-on-error -interaction=batchmode -output-directory=$(BUILDDIR)
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

NULL		:=
SPACE		:= $(NULL) $(NULL)
$(SPACE)	:= $(SPACE)# $( ) is a space
TAB		:= $(NULL)	$(NULL)
$(TAB)		:= $(TAB)# $(	) is a tabulation
define \n


endef
# $(\n) is new line
COMMA		:= $(NULL),$(NULL)

define PARALLEL_message

Invoking clean or ultraclean with other goals is not supported because of race
conditions when using parallel make
endef

ifneq ($(words $(MAKECMDGOALS)),1)
ifneq ($(filter clean ultraclean,$(MAKECMDGOALS)),)
$(error $(PARALLEL_message))
endif
endif

# Verbosity
MAXV			:= 2

ifeq ($(V),0)

Q	:= @
ECHO	:= @echo
P	:= &> /dev/null

else ifeq ($(V),1)

Q	:=
ECHO	:= @printf '\n************************************************************************\n'; echo
P	:= &> /dev/null

else

Q	:=
ECHO	:= @printf '\n************************************************************************\n'; echo
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
  foo (or $(BUILDDIR)/foo.pdf)		build the $(BUILDDIR)/foo.pdf document
  all					build all documents
  clean					delete generated files except PDFs of documents
  ultraclean				delete all generated files (but not the build directory)

Main make variables (current value):
  SHELL					must be bash ($(SHELL))
  MAXPASSES				max number of compilations, minimum 2 ($(MAXPASSES))
  FIGDIR				figures source directory ($(FIGDIR))
  BUILDDIR				build directory ($(BUILDDIR))
  V					verbosity - 0...$(MAXV) ($(V))
  LATEX					compiler ($(LATEX))

Please see mli.mk for other customizable variables.

Buildable documents:
  $(foreach t,$(sort $(TARGETS)),$(	)$(t) ($(BUILDDIR)/$(t).pdf)$(\n))
Please report bugs or suggestions of improvements to:
  Renaud Pacalet <renaud.pacalet@telecom-paristech.fr>
endef
export HELP_message

define MAXPASSESLESS2_message

Sorry, MAXPASSES ($(MAXPASSES)) must be larger or equal 2.
endef
export MAXPASSESLESS2_message

define MAXPASSES_message

Sorry, $(MAXPASSES) passes were not sufficient to build the document. Keeping
two last versions of each generated files. Comparing them may help
understanding the problem. Example:
  diff $(BUILDDIR)/foo.aux $(BUILDDIR)/foo.aux.1
Alternately, you can also try to increase the maximum number of passes:
  make MAXPASSES=5 foo
or the verbosity level:
  make V=0...$(MAXV) foo
endef
export MAXPASSES_message

# Targets
help::
	@echo "$$HELP_message"

all: $(PDFTARGETS)

.PHONY: $(TARGETS)

$(TARGETS):
	@$(MAKE) --no-print-directory $(BUILDDIR)/$@.pdf

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

# xfig to PDF (figure without text) and LaTeX (text and included PDF). Pattern
# rule to instruct make than one single recipe execution builds the two targets.
# $(1): figures source directory
# $(2): figure base name (xfig file is $(1)/$(2).fig)
define XFIGTOPDF_rule

$(BUILDDIR)/$(2)-fig%pdf $(BUILDDIR)/$(2)-fig%pdf_t: $(1)/$(2).fig
ifeq ($(FIG2DEV),)
	@echo "$$$$FIG2DEVNOTFOUND_message"
else
	$(ECHO) "[FIG2DEV]	$$< -> $(BUILDDIR)/$(2)-fig.pdf $(BUILDDIR)/$(2)-fig.pdf_t"
	$(Q)$(FIG2DEV) $(FIG2DEVFLAGS) -L pdftex $$< $(BUILDDIR)/$(2)-fig.pdf $(P) && \
	$(FIG2DEV) $(FIG2DEVFLAGS) -L pdftex_t -p $(BUILDDIR)/$(2)-fig.pdf $$< $(BUILDDIR)/$(2)-fig.pdf_t $(P)
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
	@echo "$$$$DOTNOTFOUND_message"
else
	$(ECHO) '[DOT]		$$< -> $$@'
	$(Q)$(DOT) $(DOTFLAGS) -o$$@ $$< $(P)
endif

DOTPDFS	+= $(BUILDDIR)/$(2)-dot.pdf

endef
$(foreach d,$(FIGDIR),$(foreach f,$(patsubst $(d)/%.dot,%,$(wildcard $(d)/*.dot)),$(eval $(call DOTTOPDF_rule,$(d),$(f)))))

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

$(BUILDDIR)/$(2)-dia%eps $(BUILDDIR)/$(2)-dia%pdf: $(1)/$(2).dia
ifeq ($(DIA),)
	@echo "$$$$DIANOTFOUND_message"
else ifeq ($(EPSTOPDF),)
	@echo "$$$$EPSTOPDFNOTFOUND_message"
else
	$(ECHO) '[DIA]		$$< -> $(BUILDDIR)/$(2)-dia.pdf'
	$(Q)$(DIA) $(DIAFLAGS) -e $(BUILDDIR)/$(2)-dia.eps $$< $(P) && \
	$(EPSTOPDF) $(EPSTOPDFFLAGS) --outfile=$(BUILDDIR)/$(2)-dia.pdf $(BUILDDIR)/$(2)-dia.eps $(P)
endif

DIAEPSS	+= $(BUILDDIR)/$(2)-dia.eps
DIAPDFS	+= $(BUILDDIR)/$(2)-dia.pdf

endef
$(foreach d,$(FIGDIR),$(foreach f,$(patsubst $(d)/%.dia,%,$(wildcard $(d)/*.dia)),$(eval $(call DIATOPDF_rule,$(d),$(f)))))

##############
# EPS to PDF #
##############

define EPSTOPDFNOTFOUND_message

Could not find epstopdf. Cannot produce the PDF from EPS sources. Please install epstopdf
and point your PATH to the epstopdf executable. Alternately you can also pass the epstopdf
variable wen invoking make:
  make EPSTOPDF=/opt/bin/epstopdf foo
endef
export EPSTOPDFNOTFOUND_message

define EPSTOPDF_rule

$(BUILDDIR)/$(2)-eps.pdf: $(1)/$(2).eps
ifeq ($(EPSTOPDF),)
	@echo "$$$$EPSTOPDFNOTFOUND_message"
else
	$(ECHO) '[EPSTOPDF]	$$< -> $$@'
	$(Q)$(EPSTOPDF) $(EPSTOPDFFLAGS) --outfile=$$@ $$< $(P)
endif

EPSPDFS	+= $(BUILDDIR)/$(2)-eps.pdf

endef
$(foreach d,$(FIGDIR),$(foreach f,$(patsubst $(d)/%.eps,%,$(filter-out $(DIAEPSS),$(wildcard $(d)/*.eps))),$(eval $(call EPSTOPDF_rule,$(d),$(f)))))

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
	@echo "$$$$GNUPLOTNOTFOUND_message"
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
	@echo "$$$$SVGTOPDFNOTFOUND_message"
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
	@if (( $(MAXPASSES) + 0 < 2 )); then \
       		echo "$$MAXPASSESLESS2_message"; \
		exit -1; \
	fi; \
	$(MAKE) --no-print-directory N=1 $*_latex || exit -1; \
	if [[ -f $(BUILDDIR)/$*.aux ]] && egrep -q '(\\citation)|(\\bibdata)|(\\bibstyle)' $(BUILDDIR)/$*.aux; then \
		$(MAKE) --no-print-directory $*_bibtex; \
	fi; \
	for (( i = 2; i <= $(MAXPASSES); i += 1 )); do \
		for e in $(TO_STOP_SUFFIXES); do \
			if [[ -f $(BUILDDIR)/$*.$$e ]]; then \
				cp -f $(BUILDDIR)/$*.$$e $(BUILDDIR)/$*.$$e.1; \
			fi; \
		done; \
		$(MAKE) --no-print-directory N=$$i $*_latex || exit -1; \
		for e in $(TO_STOP_SUFFIXES); do \
			if [[ -f $(BUILDDIR)/$*.$$e ]]; then \
				if [[ -f $(BUILDDIR)/$*.$$e.1 ]] && ! diff --brief $(BUILDDIR)/$*.$$e $(BUILDDIR)/$*.$$e.1 &> /dev/null; then \
					continue 2; \
				fi; \
			fi; \
		done; \
		rm -f $(patsubst %,$(BUILDDIR)/%.1,$(TO_STOP_SUFFIXES)); \
		egrep -i '(warning)|(error)' $(BUILDDIR)/$*.log | egrep -v '(infwarerr)|(LaTeX Font Warning:)|(float specifier changed to)|(Package inputenc Warning:)'; \
		break; \
	done; \
	if (( i > $(MAXPASSES) )); then \
		echo "$$MAXPASSES_message"; \
		exit -1; \
	fi

$(BUILDDIR):
	$(ECHO) '[MKDIR]		$(BUILDDIR)'
	$(Q)mkdir -p $(BUILDDIR)

clean:
	$(ECHO) '[RM]		figures and temporary files'
	$(Q)rm -f $(FIGURES) $(TO_CLEAN_FILES)

ultraclean: clean
	$(ECHO) '[RM]		$(PDFTARGETS)'
	$(Q)rm -f $(PDFTARGETS)
