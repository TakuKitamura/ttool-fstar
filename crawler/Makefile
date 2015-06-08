CR_PATH = $(shell /bin/pwd)
SRC = $(CR_PATH)/src

LIB = bin
SOURCEPATH = -sourcepath
CLASSPATH = -cp

TXT = webcrawler.txt
CLIENT_TXT=client.txt

all:
	cd $(SRC); javac $(CLASSPATH) .:../$(LIB)/* myutil/externalSearch/*.java web/crawler/*.java
	cd $(SRC); jar cmf $(TXT) ../$(LIB)/crawler.jar myutil/externalSearch/*.class web/crawler/WebCrawler.class web/crawler/*.class 
	cd $(SRC); jar cmf $(CLIENT_TXT) ../$(LIB)/client.jar myutil/externalSearch/*.class web/crawler/WebCrawler.class web/crawler/*.class 
	
clean:
	rm -f $(SRC)/web/crawler/*.class 
	rm -f $(SRC)/*.log
	rm -f $(SRC)/myutil/externalSearch/*.class

