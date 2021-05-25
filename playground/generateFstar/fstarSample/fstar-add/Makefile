all:
	krml -verify -warn-error +9 -drop WasmSupport -tmpdir ./out -fsopt --cache_dir -fsopt ./out -o ./out/add -no-prefix Add add.fst main.c && ./out/add
exec:
	krml -warn-error +9 -drop WasmSupport -tmpdir ./out -fsopt --cache_dir -fsopt ./out -o ./out/add -no-prefix Add add.fst main.c && ./out/add