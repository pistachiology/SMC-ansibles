# FRESCO 


## Dependency

all dependencies locate under folder `/src/`

## Usage

### Server deployment

To deploy server clone vm "template", run and get vm ip address then edit file hosts enter ip-address of server

run 
`ansible-playbook -i hosts fresco.yml --ask-become-pass`

enter sudo password and wait


### Run Example

```/bin/sh
$ cd /src/
$ javac -cp ./fresco-0.2-SNAPSHOT-jar-with-dependencies.jar demo/AESDemo.java 
```

On first screen
``` 
$ java -cp demo:fresco-0.2-SNAPSHOT-jar-with-dependencies.jar AESDemo -i1 -s dummy -p1:localhost:9292 -p2:localhost:9994 -in 000102030405060708090a0b0c0d0e0f
````

On another screen
```
$ java -cp demo:fresco-0.2-SNAPSHOT-jar-with-dependencies.jar AESDemo -i1 -s dummy -p1:localhost:9292 -p2:localhost:9994 -in 000102030405060708090a0b0c0d0e0f
```

