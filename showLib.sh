#!/bin/sh
rm -fr fa-tmp
unzip -d fa-tmp felix-annotation-processor.zip >/dev/null
unzip -d fa-tmp/p fa-tmp/felix-annotation-processor/lib/felix-annotation-processor.jar >/dev/null
find fa-tmp/p -name *.jar | xargs unzip -d fa-tmp/pp
