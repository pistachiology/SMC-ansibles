# TinyLEGO

## Type: Framework

## Examples

```sh

[Machine A]  $ ./build/release/Tinyconst -n 100 -c aes -e 8,4,2 -ip [A's IP] -p [port_num]
[Machine B]  $ ./build/release/Tinyeval -n 100 -c aes -e 8,4,2 -ip [A's IP] -p [port_num]

```

## Tested On Ubuntu 16.10

### Computation Schemes

| Arithematic Sharing | Boolean Sharing |  Yao's Garbled Circuit |
| :-----------------: | :-------------: | :--------------------: |
|                     |                 |           X            |

### Number of parties: 2PC

### Compiler: Yes

### [Official Site](https://github.com/AarhusCrypto/TinyLEGO)

### [Source code](https://github.com/AarhusCrypto/TinyLEGO)

### [Paper](http://eprint.iacr.org/2015/309.pdf)

### Language (or Similar to): C++