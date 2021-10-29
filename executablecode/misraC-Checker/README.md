# misraC-Checker
Generate Misra C rules and check code with Misra C linter

## Setup

##### install Python3 (>=3.3 ?)
Read Python3 documents.

##### install 'cppcheck' command
macOS
```
$ brew install cppcheck
```

linux (not tested)
```
$ sudo apt install -y cppcheck
```

## Run
```sh
$ python3 main.py example.c
Downloading https://github.com/danmar/cppcheck/archive/refs/heads/main.zip ...
Download complete
Extracting . ...
Extract complete
Delete zip file complete
Downloading https://gitlab.com/MISRA/MISRA-C/MISRA-C-2012/Example-Suite/-/archive/master/Example-Suite-master.zip ...
Download complete
Extracting . ...
Extract complete
Delete zip file complete
generate misrac-2012.txt complete
> /usr/local/bin/cppcheck --dump example.c
Checking example.c ...

> python3 cppcheck-main/addons/misra.py --rule-texts=misrac-2012.txt example.c.dump
[example.c:1] (style) The Standard Library input/output routines shall not be used (Required) [misra-c2012-21.6]
Checking example.c.dump...
Checking example.c.dump, config ...

MISRA rules violations found:
        Required: 1

MISRA rules violated:
        misra-c2012-21.6 (-): 1
```