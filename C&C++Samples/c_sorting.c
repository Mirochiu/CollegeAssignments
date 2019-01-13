#include <stdio.h>
#include <stdlib.h>

#define ARR_LEN 5

typedef struct{
	int m_i;
	char m_c;
} my_struct;


int compare_for_int(const void* a, const void* b)
{
	return (*(int*)a > *(int*)b);
}

// we compare the larger structure by the memeber m_i
int compare_for_struct(const void* a, const void* b)
{
	return ((my_struct*)a)->m_i > ((my_struct*)b)->m_i;
}

int main (int argc, char** argv)
{
	int i;
	int arr[ARR_LEN] = {2,4,1,8,10};
	my_struct* arr_struct;
	
	srand(time(NULL));
		
	// demo for int
	puts("orig data:");
	for (i=0 ; i<ARR_LEN ; ++i)
		printf("%d, ", arr[i]);
	puts("");
	
	qsort((void*)arr, ARR_LEN, sizeof(int), compare_for_int);
	
	puts("sorted data:");
	for (i=0 ; i<ARR_LEN ; ++i)
		printf("%d, ", arr[i]);
	puts("");


	// demo for structures 
	arr_struct = (my_struct*)malloc(sizeof(my_struct)*ARR_LEN);
	
	// init random data 
	for (i=0 ; i<ARR_LEN ; ++i)
		arr_struct[i].m_i = rand()%100+1;
	
	puts("orig struct data:");
	for (i=0 ; i<ARR_LEN ; ++i)
		printf("%d, ", arr_struct[i].m_i);
	puts("");
	
	qsort((void*)arr_struct, ARR_LEN, sizeof(my_struct), compare_for_struct);
	
	puts("sorted struct data:");
	for (i=0 ; i<ARR_LEN ; ++i)
		printf("%d, ", arr_struct[i].m_i);
	puts("");
  
	free(arr_struct);

	system("PAUSE");
	return 0;
}
