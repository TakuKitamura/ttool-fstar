all:
	cbindgen --version || cargo install --force cbindgen
	cargo build --release && cbindgen --config cbindgen.toml --lang c --crate rust --output include/rust.h 