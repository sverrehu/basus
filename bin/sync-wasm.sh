#!/bin/bash

SSH_TARGET=basus.no:/usr/local/share/htdocs.basus/wasm

rsync -av --protect-args --delete "./wasm/target/wasm/" "$SSH_TARGET/"
