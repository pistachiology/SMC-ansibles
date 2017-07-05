# ABY

## Type: Framework

## Dependency

all dependencies locate under folder `/src/`

## Usage

### Server deployment

To deploy server clone vm "template", run and get vm ip address then edit file hosts enter ip-address of server

run 
`ansible-playbook -i hosts aby.yml --ask-become-pass`

enter sudo password and wait


### Run Example

```sh
cd /src/ABY/bin
```

On first screen

```sh
./millionaire_prob.exe -r 0 -a 0.0.0.0
````

On another screen

```sh
./millionaire_prob.exe -r 1 -a <first_screen_ip_addr>
```

### Computation Schemes

| Arithematic Sharing | Boolean Sharing |  Yao's Garbled Circuit |
| :-----------------: | :-------------: | :--------------------: |
|          X          |        X        |           X            |

### Number of parties: 2PC

### Compiler: yes

### [Paper](http://thomaschneider.de/papers/DSZ15.pdf)

### [Source code](https://github.com/encryptogroup/ABY)

### Language (or Similar to): C++