# Custom Code For Fresco

## Explain

    main is in folder src/org/custom/code/App.java
    custom code is in folder src/org/custom/code/*.java

    if you want to have more party you could change variable numberOfParties inside of demo code and then change run.sh (parameter -p<party_id>:<ip_address>:<port>) to the actual value.


## Run examples

Compile Code

```sh
mvn clean compile
```

On first screen

```sh
# ./run.sh <party_id> <input>
./run.sh 1 1000
```


On second screen

```sh
./run.sh 2 10000
```

On third screen

```sh
./run.sh 3 1000
```