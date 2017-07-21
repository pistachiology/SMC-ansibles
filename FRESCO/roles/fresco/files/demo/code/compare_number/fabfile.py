#!/usr/bin/python

# sudo pip install fabric
from fabric.api import local

def gen():
    local("java -cp target/classes:lib/fresco-0.2-SNAPSHOT-jar-with-dependencies.jar org.custom.code.EconomicDispatchPreprocessor")

def run(n):
    local("java -cp target/classes:lib/fresco1.2-SNAPSHOT-jar-with-dependencies.jar org.custom.code.App -l INFO -p1:localhost:12345 -p2:localhost:12346 -p3:localhost:12347  -s bgw -Dbgw.threshold=1 -i %s" % (n) )