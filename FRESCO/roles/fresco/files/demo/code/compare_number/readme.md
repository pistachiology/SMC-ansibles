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

## Custom Input 

Edit file `GeneratePreprocessedInputDemo.java` for create demo custom input

## Class PreprocessGenerator

### Constructor

    `PreprocessGenerator generator = new PreprocessGenerator(inputPartyId, numberOfComputationParty, threshold);`

### function input(value, dataType={PreprocessDataType.SInt, PreprocessDataType.OInt})  

    ex. generator.input(1000, PreprocessDataType.SInt);
    
### function exportToFile()

    - export into usable from for outside input party default path should be "output/computationId-{computationId}/preprocessedInput-{inputPartyId}/"
    
## Class PreprocessedFilesInputter 

### Constructor 

     `PreprocessedFilesInputter pfi = new PreprocessedFilesInputter(1, "output/computationId-" + myId);`
          - argument 1 is number of input party 
          - argument 2 is folder of input 

### Getting an input

    - pfi.getSInt(partyId) get secret input from input party id
    - pfi.getOInt(partyId) get open input from input party id
   
## Usage

    After we edited code for generate input we can run commnad ./gen_input.sh to create preprocessedInput and then edit file source code and compile. we can just run ./run.sh as normal.
