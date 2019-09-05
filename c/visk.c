/*
Skrivet av Jakob och Johan
Den: 2019-01-29
*/
#include <stdio.h>
#include <stdlib.h>
#define storlek 64

typedef struct enBokst{
  char bokstav;
  struct enBokst* next;
}bokst;

bokst* skapaLista(char* inmatning){
  bokst* start=(bokst*)malloc(sizeof(bokst));
  bokst* temp=start;
  for (int i = 0; inmatning[i]!='\0'; i++) {
    temp->bokstav=inmatning[i];
    temp->next=(bokst*)malloc(sizeof(bokst));
    temp=temp->next;
    temp->next=NULL;
  }
  return start;
}

int vokal(char c){
  //Kollar om c är en vokal
  char vokaler[]="aeiouyåäö";
  int antalVokaler=9;
  for (int i = 0; i < antalVokaler; i++) {
    if (c == vokaler[i]){
      return 1;
    }
  }
  return 0;
}

bokst*endraLista(bokst* start){
  //Kolla om första bokstaven är en vokal
  //Ändra isf startpekaren
  int vok=vokal(start->bokstav);
  while (vok==1){
    start=start->next;
    vok=vokal(start->bokstav);
  }
  bokst* nupekare=start;
  //Kolla om resten av bokstäverna är vokaler
  //och ändra den länkade listan
  while (nupekare->next!=NULL) {
    bokst* nesta=nupekare->next;
    if (vokal(nesta->bokstav)){
      nupekare->next=nesta->next;
    }
    else{
      nupekare=nupekare->next;
    }
  }
  return start;
}

void print(bokst* pekare){
  //Skriv ut den länkade bokst-
  //listan tills nullpekaren påträffas
  while (pekare!=NULL) {
    char c=pekare->bokstav;
    printf("%c",c);
    pekare=pekare->next;
  }
  printf("\n");
}
/*
int main(){
  char inmatning[20];
  printf("%s","Vad vill du översätta till viskspråket\n");
  scanf("%s",inmatning);
  printf("Inmatning: %s\n", inmatning);
  bokst* start = skapaLista(inmatning);
  print(start);
  start=endraLista(start);
  print(start);
}
*/
int main(int argc, char *argv[], char *env[]){
        //int i=0;
	//char*argument=argv[1];
        char*argument=env[1]+13;
        printf("Content-type:text/plain; charset=utf-8\n\n");
        printf("%s\n",argument);

	bokst* start = skapaLista(argument);
  	print(start);
  	start=endraLista(start);
  	print(start);
        printf("\n");

}
