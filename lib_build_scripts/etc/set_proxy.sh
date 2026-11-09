#!/bin/bash

# usage:
# . ./set_proxy_.sh <host> <port>

# WARN!
# You MUST . or source this script for make it work for outer shell, e.g.
# `. set_proxy.sh host port`
# `source set_proxy.sh host port`
# both can make shell run script in current process rather than subprocess, so that will make env variable set as expected, and recommend use . for more compatible


host=$1
port=$2
proxy_url="http://$host:$port"

export http_proxy=$proxy_url
export https_proxy=$proxy_url
export ftp_proxy=$proxy_url
export no_proxy="localhost,127.0.0.1,$host"
