#!/bin/bash

xxd -r -ps hexdata.txt
#
# @
# &type.googleapis.com/proto3.TestMessageBexpected_value

xxd -r -ps hexdata.txt | protoscope
# 1: {
#  1: {"type.googleapis.com/proto3.TestMessage"}
#  2: {`1005420e65787065637465645f76616c756500000000`}
# }

xxd -r -ps <<<'1005420e65787065637465645f76616c756500000000' | protoscope
