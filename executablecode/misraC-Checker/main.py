# Generate Misra C rules and check code with misra-c linter

import os
import re
import urllib.request
import zipfile
import sys
import shutil

# downlaod and extract zip file
def downloadAndExtractZip(url, path):
    # download zip file
    print('Downloading {} ...'.format(url))
    sys.stdout.flush()
    urllib.request.urlretrieve(url, 'tmp.zip')
    print('Download complete')
    sys.stdout.flush()
    # extract zip file
    print('Extracting {} ...'.format(path))
    sys.stdout.flush()
    zipRef = zipfile.ZipFile('tmp.zip', 'r')
    zipRef.extractall(path)
    zipRef.close()
    print('Extract complete')
    sys.stdout.flush()
    # delete zip file
    os.remove('tmp.zip')
    print('Delete zip file complete')
    sys.stdout.flush()

# createMisra C rule
def generateMisraCRule(misra_example_path, ruleFileName, l, label):
    for fileName in l:
        file = open("{}/{}".format(misra_example_path, fileName), "r")
        fileContent = file.read()
        file.close()
        # split fileContent with '\n\n'
        fileContentList = fileContent.split('\n\n')
        targetText = fileContentList[1]
        # get chapterNumber
        chapterNumber = targetText.split('\n')[1][5:]
        commentTexts = []

        for line in (targetText.split('\n')[3:]):
            commentText = line[2:].strip()

            # end of comment text
            if (commentText == '' or commentText == '/'):
                break

            commentTexts.append(commentText)
        # join commentTexts
        commentText = ' '.join(commentTexts)
        # append write example.txt
        with open(ruleFileName, 'a') as f:
            # write text with format '{label} {chapter_number} Mandatory\n'

            # TODO: Is everything okay with 'Mandatory'?
            f.write('{} {} {}\n'.format(label, chapterNumber, 'Mandatory'))
            # write commentText
            f.write(commentText + '\n')

# main function
def main(codeCheckFileName):

    # get 'cppcheck' command path with shutil.which
    cppcheck_command_path = shutil.which('cppcheck')

    # check cppcheck command path is exist or not
    if (cppcheck_command_path is None):
        print('Error: cppcheck command is not found')
        sys.exit(1)
    

    cppcheck_path = 'cppcheck-main'
    # if not cppcheck_path directory, download and extract zip file
    if not os.path.exists(cppcheck_path):
        downloadAndExtractZip('https://github.com/danmar/cppcheck/archive/refs/heads/main.zip', '.')


    misra_example_path = 'Example-Suite-master'
    # if not 'misraC-Checker-master' directory, download and extract zip file
    if not os.path.exists(misra_example_path):
        downloadAndExtractZip('https://gitlab.com/MISRA/MISRA-C/MISRA-C-2012/Example-Suite/-/archive/master/Example-Suite-master.zip', '.')

    # get fileList from misra_example_path Directory 
    fileList = os.listdir(misra_example_path)

    # define dirRegex r'D_\d+_\d+.c'
    dirRegex = re.compile(r'D_\d+_\d+.c')

    # get dirList that matches dirRegex from fileList
    dirList = [dirRegex.search(file).group() for file in fileList if dirRegex.search(file)]

    # define ruleRegex r'R_\d+_\d+.c'
    ruleRegex = re.compile(r'R_\d+_\d+.c')

    # get ruleList that matches ruleRegex from fileList
    ruleList = [ruleRegex.search(file).group() for file in fileList if ruleRegex.search(file)]

    # define ruleFileName
    ruleFileName = 'misrac-2012.txt'

    # write example.txt named ruleFileName file
    # the first line is 'Appendix A Summary of guidelines\n' 
    # write 
    with open(ruleFileName, 'w') as f:
        f.write('Appendix A Summary of guidelines\n')
            
    # call generateMisraCRule function
    generateMisraCRule(misra_example_path, ruleFileName, ruleList, 'Rule')
    generateMisraCRule(misra_example_path, ruleFileName, dirList, 'Dir')

    # print format is 'generate {ruleFileName} complete'
    # print('generate {} complete'.format(ruleFileName))

    # define cppcheck command
    # command format is cppcheck --dump {codeCheckFileName} {cppcheck_path}/addons/misra.py --rule-texts=misrac-2012.txt {codeCheckFileName}.dump
    cppcheck_command = '{} --dump {}'.format(cppcheck_command_path, codeCheckFileName)
    # print('> ' + cppcheck_command)

    # exec cppcheck command and get command standart output
    cppcheck_result = os.popen(cppcheck_command).read()
    # print cppcheck_result
    # print(cppcheck_result)

    # misra command format is 'python3 {cppcheck_path}/addons/misra.py --rule-texts={ruleFileName} {codeCheckFileName}.dump'
    misra_command = 'python3 {}/addons/misra.py --rule-texts={} {}.dump'.format(cppcheck_path, ruleFileName, codeCheckFileName)

    # print misra_command
    # print('> ' + misra_command)

    # exec misra_command command and get command standart output
    misra_result = os.popen(misra_command).read()

    # print misra_result
    print(misra_result)
    if "MISRA rules violations found" in misra_result:
        print("'{}' has MISRA rules violations\n".format(codeCheckFileName))
        return 1
    #     sys.exit(1)
    return 0

if __name__ == '__main__':
    # check argc is 2 or not
    if (len(sys.argv) < 2):
        print('usage: python3 main.py <c or cpp file path> ...')
        sys.exit(1)

    exit_status = []
    # call main function
    for codeCheckFileName in sys.argv:
        if codeCheckFileName != "main.py":
            exit_status.append(main(codeCheckFileName))

    for status in exit_status:
        if status != 0:
            print("fix some MISRA rules violations")
            sys.exit(1)
    sys.exit(0)