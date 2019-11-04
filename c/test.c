#include <stdio.h>

int main(int argc, char *argv[], char *env[]){
	int i=0;
	char*argument=env[1]+13;
	printf("Content-type:text/plain; charset=utf-8\n\n");
	//printf("%s",argument);
	while(*argument!='\0'){
		printf("%c",*argument);
		argument++;
	}
	printf("\n");
	i=0;
/*
	while(env[i]){
		printf("env[%d]=%s\n",i,env[i]);

		i++;
	}

*/
}
