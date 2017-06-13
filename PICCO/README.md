# PICCO

# How to build 
- create virtual machine ( tested on Ubuntu 16.04 )
- run `ansible-playbook -i hosts main.yml -K (-vvv for verbose)`
- login into machine go to path /src/picco/compiler
- run `./compile.sh`


# How to run PICCO example ( based on matrix-multiple.c)

## Step 0: Go to picco directory
- `mkdir -p /src/picco/examples`
- `cd /src/picco/examples`

## Step 1: compile source code
- prepare file
```sh
$ cp /src/picco/compiler/sample-code/test-code/non-thread/matrix-multiply.c ./matrix-multiply-raw.c
$ cp /src/picco/compiler/smc_config ./smc_config
```

- compile 
```sh
$ picco matrix-multiply-raw.c smc-config matrix-multiply utilconf
```

## Step 2: create input file

From code matrix-multiply-raw.c we see that we have `smc_input(A, 1, S, S)`, `smc_input(B, 1, S, S)` and `smc_output(C, 1, S, S)` which mean we have 1 input party and 1 output party (in the same machine id:1) and 1 computation party (id:2) so we're going to write input for id:1 
- create input file `input_p1` 
### input_p1
```
A = 1, 1
A = 1, 1
B = 2, 2
B = 2, 2
```

## Step 3: generate shared input
now we need to generate share input by using command
`picco-utility -I [id] input_file [util_conf] [base_output_filename]`

![alt text][generate_share_input]
from image you'll see that there're 3 files (share_p1_[1..3]) because in smc_config we set peers to 3. 

## Step 4: generate executable binary from compiled source code. 
This step will be a little bit tricky because makefile in folder `/src/picco/compute` make for file test-code.cpp.
```
$ cp matrix-multiply.cpp ../compute/test-code.cpp
$ cd ../compute/
$ make clean; make
$ mv test-code ../examples/matrix-multiply
$ cd - 
```

## Step 5: create runtime config

We need to create runtime config to assign ip and port for each party to know each other.

In this example, We create file runtimeconf and run all parties in the same machine

## runtimeconf
```
1,localhost,10001
2,localhost,10002
3,localhost,10003
```
`format: [id] [ip-addr] [port]`

## Step 6: run server
   we need to run N + 1 instances. 1 instance for a seed program that will be used to generate and transmit secret random seeds to each of the computational parties at the
time of program initiation

run command in order. 

Computer 3: `./matrix-multiply 3 runtimeconf 1 1 share_p1_3 output_p1_`

Computer 2: `./matrix-multiply 2 runtimeconf 1 1 share_p1_2 output_p1_`

Computer 1: `./matrix-multiply 1 runtimeconf 1 1 share_p1_1 output_p1_`

Computer 4: ` picco seed runtimeconf `

`Usage: <id> <runtime-config> <number-of-input-parties> <number-of-output-parties> <input-share> <output>`


## Step 7: Merge shared output

run command

`picco-utility -O 1 output_p1_ utilconf result_p1` 

`cat result_p1`

![alt text][run_and_merge]


# Note

\- 

# TODO:
\-




[generate_share_input]: ./imgs/generate_share_input.png
[run_and_merge]: ./imgs/run_and_merge.png