#!/bin/bash

# protoscope
# protobuf 데이터를 시각적으로 분석하고 디버깅하는 도구
echo -e "\n"
cat hexdata.txt
echo -e "\n"
xxd -r -ps hexdata.txt | protoscope
# 1: {
#   1: {"type.googleapis.com/proto3.TestMessage"}
#   2: {`1005420e65787065637465645f76616c756500000000`}
# }

echo -e "\n"
echo '\x10\x05B\x0eexpected_value\x00\x00\x00\x00'
printf '\x10\x05B\x0eexpected_value\x00\x00\x00\x00' | protoscope
