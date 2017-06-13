public int PEER_NUM = 4;
public int main() {
        /*******************/
   private int A, B, C, D;
   private int arr[5];
   private int O;
   private int *ptr_O;
   public int i, j;

   smcinput(A, 1);
   smcinput(B, 2);
   smcinput(C, 3);
   smcinput(D, 4);
   arr[1] = A;
   arr[2] = B;
   arr[3] = C;
   arr[4] = D;

   O = 1;
   ptr_O = &O;

   for(i = 1; i <= 4; i++){
       if(arr[i] > arr[O]) {
           O = i;
       }
   }

   smcoutput(O, 1);
 
   return 0;
}

