#include <stdio.h>
#include <math.h>




double f(double x) {
	return x*x*x*x - 2*x*x*x -4*x*x + 4*x + 4;
}


int main()
{
	double x0,x1,fx0,fx1;
	double nx;
	int i,j;
	
	for (j=-10 ; j<=0 ; j++) {
		printf("J = %d\n",j);
		x0 = j;
		x1 = j+1;
		for (i=0 ; i<10 ; i++) {
			fx0 = f(x0);
			fx1 = f(x1);
			nx = x1 + fx1 * (x1-x0)/(fx1-fx0);
			printf("X%d=%.8f \t f(x)=%.8f \n",i,x1,fx1);
			x0=x1;
			x1=nx;
		}
	}
	return 0;
}
