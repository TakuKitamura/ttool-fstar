all:
	make -C fstar && cd misraC-Checker && python3 main.py `ls ../misrac/*.c` && cd .. && make -f Makefile.compile

clean:
	rm -rf lib fstar/out rust/target rust/include generated_src/extern.h misrac/*.c.ctu-info misrac/*.c.dump misraC-Checker/cppcheck-main misraC-Checker/Example-Suite-master misraC-Checker/misrac-2012.txt
