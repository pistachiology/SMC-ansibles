# PCF

## Type: Framework

## Usage

```sh
 lcc -c -S -target=bytecode program.c -o program.lcc
 translate.sh ./translate.sh program.lcc program.pcf2 # < stuck
  ./simulate.sh program.pcf2 inputfile.txt
```

## Tested On Ubuntu 16.10 

### Computation Schemes

| Arithematic Sharing | Boolean Sharing |  Yao's Garbled Circuit |
| :-----------------: | :-------------: | :--------------------: |
|                     |                 |           X            |

### Number of parties: Multiparty

### Compiler: yes

### [Official Site](https://www.usenix.org/conference/usenixsecurity13/technical-sessions/paper/kreuter)

### [Source code](https://github.com/cryptouva/pcf/)

### [Paper](https://www.usenix.org/system/files/conference/usenixsecurity13/sec13-paper_kreuter.pdf)

### Language (or Similar to): C