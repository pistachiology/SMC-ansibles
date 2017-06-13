# ABY


## Dependency

all dependencies locate under folder `/src/`


## Usage

### Server deployment

To deploy server clone vm "template", run and get vm ip address then edit file hosts enter ip-address of server

run 
`ansible-playbook -i hosts aby.yml --ask-become-pass`

enter sudo password and wait


### Run Example

```/bin/sh
$ cd /src/ABY/bin
```

On first screen
``` 
$ ./millionaire_prob.exe -r 1
````

On another screen
```
$ ./millionaire_prob.exe -r 0
```

