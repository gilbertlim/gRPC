#!/bin/bash

# hex dump (16진수로 출력)
xxd helloworld.txt
# 00000000: 6865 6c6c 6f20 776f 726c 64              hello world
# 상대주소:   2바이트씩 묶어서 출력                         ASCII 형태 출력

# 16진수로 표현된 데이터를 ascii 문자열로 변환
xxd -r -ps helloworld-binary.txt
# hello world

# protoscope
# protobuf 데이터를 시각적으로 분석하고 디버깅하는 도구, 역직렬화
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
