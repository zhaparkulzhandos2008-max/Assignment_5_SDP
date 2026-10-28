#!/bin/sh
set -eu
cd "$(dirname "$0")"
mkdir -p target/classes
find src/main/java -name '*.java' > target/sources.txt
javac --release 17 -encoding UTF-8 -d target/classes @target/sources.txt
java -cp target/classes kz.aitu.delivery.Main
