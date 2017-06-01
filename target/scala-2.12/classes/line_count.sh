#!/usr/bin/env bash
cd ../../src
src_lines="$(git ls-files | args cat | wc -l)"
cd ../res
res_lines="$(git ls-files | sed '/font/d' | wc -l)"
echo $src_lines