# Tools
LATEXFLAGS	?= -interaction=nonstopmode -file-line-error
PDFLATEX	= pdflatex $(LATEXFLAGS)
BIBTEX		= bibtex
FIG2DEV		= fig2dev
RUBBER		?= $(shell which rubber 2> /dev/null)
RUBBERFLAGS	= --pdf --short --warn refs --warn misc -q

# Files and directories
FIGDIR		= figures
FIGS		= $(wildcard $(FIGDIR)/*.fig)
FIGPDFS		= $(patsubst $(FIGDIR)/%.fig,$(FIGDIR)/%.pdf,$(FIGURES))
FIGPDFTS	= $(patsubst $(FIGDIR)/%.fig,$(FIGDIR)/%.pdf_t,$(FIGURES))
FIGURES		= $(filter-out $(FIGS) $(FIGPDFS) $(FIGPDFTS), $(wildcard $(FIGDIR)/*))

JOB	= codegeneration_documentation
TEXS	= $(wildcard *.tex) $(wildcard *.sty) $(wildcard *.cls)
PICS	= $(wildcard *.png) $(filter-out $(JOB).pdf,$(wildcard *.pdf))
BIBS	= $(wildcard *.bib) $(wildcard *.bst)

TOPS		= $(shell grep -l '[^%]*\\begin{document}' *.tex)
PDFTARGETS	= $(patsubst %.tex,%.pdf,$(TOPS))
TARGETS		= $(patsubst %.tex,%,$(TOPS))

.DEFAULT: help
.PHONY: all help clean ultraclean

help:
	@echo '----------------------------------------------------------------'; \
	echo 'available targets:'; \
	echo '  "make" or "make help":        print this help'; \
	echo '  "make foo" or "make foo.pdf": build the foo.pdf document'; \
	echo '  "make all":                   build all documents'; \
	echo '  "make clean":                 delete non-essential generated files'; \
	echo '  "make ultraclean":            delete all generated files'; \
	echo '----------------------------------------------------------------'; \
	echo 'Buildable documents:'; \
	echo -n $(sort $(TARGETS)) | sed -e 's/\([^ ]\+\) */  \1\n/g'; \
	echo '----------------------------------------------------------------'; \
	echo 'if you encounter problems please contact:'; \
	echo '  Renaud Pacalet <renaud.pacalet@telecom-paristech.fr>'; \
	echo '----------------------------------------------------------------'

all: $(PDFTARGETS)
$(TARGETS): % : %.pdf

ifeq ($(RUBBER),)

# Bootstrap aux file, then keep running pdflatex until it reaches a fixpoint

$(JOB).aux: | $(TEXS) $(PICS)
	$(PDFLATEX) $(JOB)

$(JOB).bbl: $(JOB).aux $(BIBS)
	$(BIBTEX) $(JOB)

$(JOB).pdf: $(TEXS) $(PICS) $(JOB).aux $(JOB).bbl
	@cp -p $(JOB).aux $(JOB).aux.bak
	$(PDFLATEX) $(JOB)
	@if cmp -s $(JOB).aux $(JOB).aux.bak; \
	then touch -r $(JOB).aux.bak $(JOB).aux; \
	else NEWS="$$NEWS -W $(JOB).aux"; fi; rm $(JOB).aux.bak; \
	if [ -n "$$NEWS" ]; then $(MAKE) $$NEWS $@; fi

$(FIGDIR)/%.pdf: $(FIGDIR)/%.fig
	$(FIG2DEV) -L pdftex $< $@

$(FIGDIR)/%.pdf_t: $(FIGDIR)/%.fig $(FIGDIR)/%.pdf
	$(FIG2DEV) -L pdftex_t -p $(patsubst %.pdf_t,%.pdf,$@) $< $@

clean:
	rm -f $(JOB).aux $(JOB).log $(JOB).blg $(JOB).bbl $(JOB).out $(JOB).pdf

else

.NOTPARALLEL:

.PHONY: $(PDFTARGETS)

$(PDFTARGETS): %.pdf: %.tex
	@$(RUBBER) $(RUBBERFLAGS) $<

clean:
	@$(RUBBER) $(RUBBERFLAGS) --clean $(TOPS)

endif
