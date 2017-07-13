#!/bin/sh


# java -cp target/classes:lib/fresco-0.2-SNAPSHOT-jar-with-dependencies.jar org.customcode.compare.App -p1:localhost:12345 -p2:localhost:12346 -p3:localhost:12347 -s bgw -Dbgw.threshold=1 -i $1 -x $2
# java -cp target/classes:lib/fresco-0.2-SNAPSHOT-jar-with-dependencies.jar org.customcode.compare.App -p1:localhost:12345 -p2:localhost:12346 -p3:localhost:12347 -s spdz -i $1 -x $2
java -cp target/classes:lib/fresco-0.2-SNAPSHOT-jar-with-dependencies.jar org.custom.code.App -p1:localhost:12345 -p2:localhost:12346 -p3:localhost:12347  -s bgw -Dbgw.threshold=1 -i $1 -x $2
