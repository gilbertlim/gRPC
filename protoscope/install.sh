#!/bin/bash

go install github.com/protocolbuffers/protoscope/cmd/protoscope...@latest

echo 'export PATH="$HOME/go/bin:$PATH"' >>~/.zshrc
source ~/.zshrc
