
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
