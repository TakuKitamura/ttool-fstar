all:
	make -C fstar && cd misraC-Checker && python3 main.py `ls ../misrac/*.c` && cd.. && make -f Makefile.compile

clean:
	rm -rf lib; rm -rf fstar/out; rm -rf rust/target; rm -rf rust/include; rm -rf generated_src/extern.h
