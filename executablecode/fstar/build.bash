#/bin/bash

generate_verified_code='krml -verify -warn-error +9 -drop WasmSupport -tmpdir ./out -fsopt --cache_dir -fsopt ./out -fsopt --cache_checked_modules -skip-linking -skip-compilation'

files="*.fst"
fileList=""
for fileName in $files; do
    basename=`basename $fileName .fst`
    prefixName=`echo $basename | cut -c 1 | tr [a-z] [A-Z]``echo $basename | cut -c 2-`
    generate_verified_code="$generate_verified_code -no-prefix $prefixName"
    fileList="$fileList $fileName"
done
generate_verified_code="$generate_verified_code $fileList"
echo $generate_verified_code
$generate_verified_code