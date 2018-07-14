#include<iostream>
#include<fstream>
#include<string>
#include<cuda_runtime.h>
#include<random> 
#include<stack>
#include <malloc.h>
#include <stdlib.h>
#include <stdio.h>
#include <string>
#include <time.h>
#include <cuda.h>
#include <math.h>
using namespace std;
//larger than pow(2,25) less than pow(2,31)
#define Prime  149672669

//unsigned int  hashshift(unsigned int a)
//{
//	srand((unsigned)time(Null));
//	a = (a + rand()) + (a << 12);
//	a = a % (int)pow(2, 24)*1.2;
//	if (a < 0)
//		a += ((int)pow(2, 24)*1.2);
//	return a;
//}
__global__ void hashcal(int* d_input, int* d_hash_fun, int* d_a, int* d_b, int table_size, int set_size,int t)
{
	int i = blockDim.x * blockIdx.x + threadIdx.x;
	int j;
	if (i < set_size)
	{
		for (j = 0; j < t; j++)
		{
			d_hash_fun[i*t+j] = ((d_a[j] * d_input[i] + d_b[j]) % Prime) % table_size;
			//printf(" %d ", d_hash_fun[i*t + j]);
			
		}
		__syncthreads();
		if (i == set_size - 1)
			printf("qaq");

	}

}

__global__ void hashfun(int* d_input, int* d_hash_table, int* d_hash_fun_i, int* d_a, int* d_b,int table_size,int * d_evict,int set_size,int *d_hash_fun,int t)
{
	//calculate first or calculate them each time?
	int i = blockDim.x * blockIdx.x + threadIdx.x;
	int key_fun = d_hash_fun_i[i];
	//printf("(d_a[key_fun] * d_input[i] + d_b[key_fun]) mod Prime modtable_size=%d * %d+ %d) mod %d mod %d\n", d_a[key_fun], d_input[i], d_b[key_fun], Prime, table_size);
	int hash_val;
	//printf("thread_i %d,i %d,key_fun %d,hash_val %d\n", thread_i, i, key_fun, hash_val);
	//printf("%d,%d,%d,%d\n", sizeof(int), sizeof(d_a), sizeof(d_hash_table), hash_val);
	//printf("(d_a[key_fun] * d_input[i] + d_b[key_fun]) mod Prime modtable_size=%d * %d+ %d) mod %d mod %d\n", d_a[key_fun], d_input[i], d_b[key_fun], Prime, table_size);
	if (i < set_size)
	{
		hash_val = d_hash_fun[i*t + key_fun];
		if ((d_hash_table[hash_val] == -1 || d_evict[i] == 1) && d_input[i] != 0) {
			d_hash_table[hash_val] = d_input[i];
		}
		__syncthreads();
		//printf("hashtable[i],%d\n", d_hash_table[i]);
		//printf("1qwq%d", *out_num);
		//printf("Hello thread %d, f=%d\n", i);
	}

}

__global__ void evicter(int * d_hash_table, int * d_hash_fun_i,
	int *d_input, int *d_evict ,int *d_a, int *d_b, int table_size,int t, int set_size, int *d_outnum,int *d_hash_fun)
{
	__syncthreads();
	int thread_i = blockDim.x * blockIdx.x + threadIdx.x;
	int key_fun = d_hash_fun_i[thread_i];
	int hash_val;
	//printf("qwq%d,%d\n", d_hash_table[hash_val], d_input[thread_i]);
	if (thread_i < set_size)
	{
		hash_val = d_hash_fun[thread_i*t + key_fun];
		if ((d_hash_table[hash_val] != d_input[thread_i]) && (d_input[thread_i] != 0))
		{
			//printf("qwqhengheng%d\n", thread_i);
			d_evict[thread_i] = 1;
			d_hash_fun_i[thread_i] = (d_hash_fun_i[thread_i] + 1) % t;
			atomicAdd(d_outnum, 1);
		}
		else
		{
			d_evict[thread_i] = 0;

		}
	}
}

//use the most simple to avoid any error
void checkright(int *hash_table, int * hash_fun_i, int * input, int set_size,int table_size,int *a,int *b)
{
	int errcount = 0;
	int key_fun, hash_val;
	for (int i = 0; i < set_size; i++)
	{
		key_fun = hash_fun_i[i];
		hash_val= (a[key_fun] * input[i] + b[key_fun]) % Prime % table_size;
		//printf("(a[key_fun] * input[i] + b[key_fun]) mod Prime modtable_size=%d * %d+ %d) mod %d mod %d\n", a[key_fun], input[i], b[key_fun], Prime, table_size);
		//cout << "hashval " << hash_val << endl;
		//cout << "hash_table[hash_val] " << hash_table[hash_val] << "input[i] " << input[i]<<endl;
		//if (i > set_size-5)
		//{
		//	cout << "here " <<i<<"hashtab " << hash_table[hash_val] << "inpuit" << input[i] << "hash" << hash_val << endl;
		//	cout << _msize(hash_table) / sizeof(hash_table[0]) << endl;
		//	cout << _msize(input) / sizeof(input[0]) << endl;
		//}
		//if (i == 5) exit(0);
		if (hash_table[hash_val] != input[i])
		{
			errcount++;
		}
	}
	if (errcount == 0)
	{
		cout << "DONEqwq\n";
	}
	else {
		cout << "errcount is \n" << errcount;
	}
	
}

void cudacheck(cudaError_t err) {
	if (err != cudaSuccess) {
		cout << "Could not copy global_flag to CUDA device\n" << cudaGetErrorString(err) << endl;
		exit(0);
	}
}

int main/*qwq*/(/*int table, int set,int *input,int bound_in*/) {

	int table_size = (int)pow(2,24);
	//int table_size = table;
	int set_size = pow(2, 10);
	const int t = 3;
	int a[t], b[t];
	int round = 0;
	int outnum = pow(2, 24);
	int * hash_table = new int[table_size];

	int * input = new int[set_size];

	int * hash_fun_i = new int[set_size];
	//int * hash_fun = new int[set_size*t];
	int * hash_fun = new int[set_size*t];
	int * evict = new int[set_size];
	int sizeof_hashtable = sizeof(int)*table_size;
	int sizeof_const = sizeof(int)* t;
	int sizeof_input = sizeof(int)* set_size;
	int *d_a, *d_b, *d_hash_table, *d_input,*d_hash_fun_i,* d_outnum;
	int *d_evict, *d_hash_fun;
	int bound = 0;
	bool needhash = true;
	
	stack <float>stk;
	cudaError_t err = cudaSuccess;
	bound = (int)4 * log(table_size);
	//bound = bound_in;
	//use -1 to know it is empty for the random number is larger or equal to 0
	fill(hash_table, hash_table + table_size , -1);
	fill(hash_fun, hash_fun + set_size*t , -1);
	fill(evict, evict + set_size , 0);
	fill(hash_fun_i, hash_fun_i + set_size , 0);
	//Use std random number for rand() is not good and I suppose that the input numbers
	//have no equal number. So std random is better.
	std::default_random_engine random(time(NULL));
	//For a multiple input may be larger than INT32MAX,so let them 
	//less than INT32MAX
	std::uniform_int_distribution<int> ranab(1, pow(2,6));
	std::uniform_int_distribution<int> ranin(1, pow(2,25));
	
	for (int i = 0; i < set_size; i++)
		input[i] = ranin(random);
	for (int i = 0; i < t; i++)
	{
		a[i] = ranab(random);
		b[i] = ranab(random)-1;
		//b can be 0 but a can not.
	}
	//for (int i = 0; i < set_size; i++)
	//{
	//	for (int j = 0; j < t; j++)
	//	{
	//		hash_fun[i*t + j] = hash32shift(input[i]);
	//		//cout << hash_fun[i*t + j];
	//	}
	//}
	cudaMalloc((void **)&d_hash_table, sizeof_hashtable);
	cudaMalloc((void **)&d_input, sizeof_input);
	cudaMalloc((void **)&d_hash_fun_i, sizeof_input);
	cudaMalloc((void **)&d_hash_fun, sizeof_input*t);
	cudaMalloc((void **)&d_a, sizeof_const);
	cudaMalloc((void **)&d_b, sizeof_const);
	cudaMalloc((void **)&d_outnum, sizeof(int));
	cudaMalloc((void **)&d_evict, sizeof_input);
	err = cudaMemcpy(d_hash_table, hash_table,sizeof_hashtable, cudaMemcpyHostToDevice);cudacheck(err);
	err = cudaMemcpy(d_input, input,sizeof_input, cudaMemcpyHostToDevice);cudacheck(err);
	err = cudaMemcpy(d_hash_fun_i, hash_fun_i, sizeof_input, cudaMemcpyHostToDevice);cudacheck(err);
	err = cudaMemcpy(d_a, a, sizeof_const, cudaMemcpyHostToDevice);cudacheck(err);
	err = cudaMemcpy(d_b, b, sizeof_const, cudaMemcpyHostToDevice);cudacheck(err);
	err = cudaMemcpy(d_hash_fun, hash_fun, sizeof_input*t, cudaMemcpyHostToDevice);	cudacheck(err);
	err = cudaMemcpy(d_evict, evict, sizeof_input, cudaMemcpyHostToDevice); cudacheck(err);
	
	cudaEvent_t start, stop;
	cudaEventCreate(&start);
	cudaEventCreate(&stop);
	
	int total_round = 0;

	while (outnum!=0 && total_round <=15) {
		outnum = 0;
		err =  cudaMemcpy(d_outnum, &outnum, sizeof(int), cudaMemcpyHostToDevice); cudacheck(err);
		float elapsedTime = 0;

		if (round == bound) {
			round = 0;
			total_round++;
			for (int i = 0; i < t; i++) {
				a[i] = ranab(random);
				b[i] = ranab(random) - 1;
			}
			needhash = true;
			fill(hash_table, hash_table + table_size , -1);
			fill(evict, evict + set_size , 0);
			fill(hash_fun_i, hash_fun_i + set_size , 0);
			fill(hash_fun, hash_fun + set_size * t, -1);
			
			err = cudaMemcpy(d_a, a, sizeof_const, cudaMemcpyHostToDevice); cudacheck(err);
			err = cudaMemcpy(d_b, b, sizeof_const, cudaMemcpyHostToDevice); cudacheck(err);
			err = cudaMemcpy(d_hash_table, hash_table, sizeof_hashtable, cudaMemcpyHostToDevice); cudacheck(err);
			err = cudaMemcpy(d_evict, evict, sizeof_input, cudaMemcpyHostToDevice); cudacheck(err);
			err = cudaMemcpy(d_hash_fun_i, hash_fun_i, sizeof_input, cudaMemcpyHostToDevice); cudacheck(err);
			err = cudaMemcpy(d_hash_fun, hash_fun, sizeof_input*t, cudaMemcpyHostToDevice); cudacheck(err);
		}
		
		if (needhash == true)
		{
			cudaEventRecord(start);
			hashcal << <set_size / 256 + 1, 256 >> > (d_input, d_hash_fun, d_a, d_b, table_size, set_size, t);
			cudaEventRecord(stop);
			cudaEventSynchronize(stop);
			cudaEventElapsedTime(&elapsedTime, start, stop);
			stk.push(elapsedTime);
			//printf("hashcal using time£º%f <ms>\n", elapsedTime);
		}
		

		cudaEventRecord(start);
		hashfun << <set_size/256+1, 256 >> >(d_input, d_hash_table,d_hash_fun_i, d_a, d_b, table_size, d_evict,set_size, d_hash_fun, t);
		cudaEventRecord(stop);

		cudaEventSynchronize(stop);
		elapsedTime = 0;
		cudaEventElapsedTime(&elapsedTime, start, stop);
		stk.push(elapsedTime);
		//printf("hashfun using time£º%f <ms>\n", elapsedTime);
		 
		cudaEventRecord(start);
		evicter << <set_size / 256+1, 256 >> >(d_hash_table,  d_hash_fun_i,d_input, d_evict, d_a, d_b, table_size, t, set_size,d_outnum, d_hash_fun);
		cudaEventRecord(stop);

		//needhash = false;

		cudaEventSynchronize(stop);
		elapsedTime = 0;
		cudaEventElapsedTime(&elapsedTime, start, stop);
		stk.push(elapsedTime);
		//printf("evicter using time£º%f <ms>\n", elapsedTime);

		err = cudaMemcpy(&outnum, d_outnum, sizeof(int), cudaMemcpyDeviceToHost); cudacheck(err);

		cout << "round is "<< round <<" out is "<<outnum<<endl;
		round++;
		
	}


	cudaEventDestroy(start);
	cudaEventDestroy(stop);
	float tim = 0;
	while (!stk.empty())
	{
		tim += stk.top();
		stk.pop();
	}
	cout << "time is " << tim<<" ms"<<endl;
	err = cudaMemcpy(hash_table, d_hash_table, sizeof_hashtable, cudaMemcpyDeviceToHost); cudacheck(err);
	err = cudaMemcpy(hash_fun_i, d_hash_fun_i, sizeof_input, cudaMemcpyDeviceToHost); cudacheck(err);

	checkright(hash_table,hash_fun_i,input,set_size, table_size,a,b);

	while(!stk.empty())
    {
        cout<<stk.top()<<endl;
        stk.pop();
    }

	cudaFree(d_outnum);
	cudaFree(d_input);
	cudaFree(d_hash_table);
	cudaFree(d_hash_fun_i);
	cudaFree(d_a); cudaFree(d_b);
	cudaFree(d_evict);
	cudaFree(d_hash_fun);
	//delete[] input;
	delete[] hash_table;
	delete[] hash_fun_i; 
	delete[] evict;
	delete[] hash_fun;
	return 0;
}

//int main() {
//	for (int i = 1; i <= 5; i++)
//	{
//	std::default_random_engine random(time(NULL));
//	std::uniform_int_distribution<int> ranin(1, pow(2, 25));
//	int * input = new int[pow(2, 24)];
//	for (int i = 0; i < pow(2,24); i++)
//		input[i] = ranin(random);
//	
//	float k[1] = { 1.2 };
//	
//	for (int j = 0; j <=0; j += 1) {
//		cout << "table_size is " << j << "n" << endl;
//		int table_size = (int)(pow(2, 24) *k[j]);
//		int bound = (int)4*log(table_size);
//		cout << log(table_size) << endl;
//			qwq(table_size, 24, input,bound);
//		}
//		cout << "---------------------" << endl;
//	delete[] input;
//	}
//}

//int main() {
//	for (int i = 1; i <= 5; i++)
//	{
//		std::default_random_engine random(time(NULL));
//		std::uniform_int_distribution<int> ranin(1, pow(2, 25));
//		int * input = new int[pow(2, 24)];
//		for (int i = 0; i < pow(2, 24); i++)
//			input[i] = ranin(random);
//		float k[3] = { 1.01,1.02,1.05 };
//		for (int j = 0; j <= 2; j += 1) {
//			cout << "table_size is " << j << "n" << endl;
//			int table_size = (int)(pow(2, 24) *k[j]);
//			qwq(table_size, 24, input);
//		}
//		cout << "---------------------" << endl;
//		delete[] input;
//	}
//}

//int main() {
//for (int i = 1; i <= 5; i++)
//	{
//	for (int j = 10; j <= 24; j++) {
//	cout << "the set size is " << j<<endl;
//	qwq(25, j);
//	}
//	cout << "---------------------" << endl;
//	}
//}

//int main() {
//for (int i = 1; i <= 5; i++)
//		{
//		std::uniform_int_distribution<int> ranin(1, pow(2, 25));
//		std::uniform_int_distribution<int> ranchoose(0, pow(2, 24));
//		std::default_random_engine random(time(NULL));
//		int set_size = pow(2, 24);
//		//cout << "the set size is " << j<<endl;
//		
//			int * inputold = new int[set_size];
//			
//			for (int k = 0; i < set_size; i++)
//				inputold[i] = ranin(random);
//			for (int j = 100; j >= 0; j -= 10)
//			{
//				int * input = new int[set_size];
//				cout << set_size * j / 100 << endl;
//				for (int k = 0; k < set_size*j/100; k++)
//					input[k] = inputold[ranchoose(random)];
//				for (int k = set_size * j / 100 ; k < set_size; k++)
//					input[k] = ranin(random);
//
//				qwq(25, 24, input);
//				delete[] input;
//			}
//			
//			delete[] inputold;
//			
//
//		}
//		cout << "---------------------" << endl;
//		
//
//	
//
//}