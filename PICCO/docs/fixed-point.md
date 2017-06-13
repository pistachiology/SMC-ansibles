# Fixed-point 



## Code

```c
#include<math.h>

public int K=20; // length of array / number of input elements
public int M = 5; 
public int N = 4; 

public int main()
{
	
	private float<32, 20> A, B, C, D;
	private float<32, 20> OUT;

	smcinput(A, 1);
	smcinput(B, 2);
	smcinput(C, 3);
	smcinput(D, 4);
	OUT = A * B * C * D;
	smcoutput(OUT, 1);
	return 0;
}
```

## INPUT AND OUTPUT

INPUT 
```
A=1.1134556
B=2.22
C=3.33
D=4.44
```

OUTPUT
```
OUT=36.5471
```
## __Screenshots__

![alt text][fixed-point]

[fixed-point]: ../imgs/fixed-point.png