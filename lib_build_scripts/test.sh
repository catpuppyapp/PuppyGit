#!/bin/bash

# set default value
default_archs="x86 arm64"

# if cli has value, use it, else use default
archs=${1:-$default_archs}


for arch in $archs; do
  echo "Current arch: $arch"
  
done
