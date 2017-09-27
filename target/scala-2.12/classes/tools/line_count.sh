#!/usr/bin/env bash
cd ../../src
src_lines="$(git ls-files | xargs cat |wc -l)"
cd ../res
res_lines="$(git ls-files | sed '/font/d' | xargs cat | wc -l)"
echo $(($src_lines + $res_lines))