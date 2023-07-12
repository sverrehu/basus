#!/bin/bash

SSH_TARGET=pg2.thathost.com:/usr/local/share/htdocs.basus/wasm

rsync -av --protect-args --delete "./wasm/target/wasm/" "$SSH_TARGET/"
