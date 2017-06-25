#include <iostream>
#include <stdio.h>

#include "Baza.h"
#include "IPolaczenie.h"


using namespace std;


void inicjujBaze(IPolaczenie* pol) {
  const std::string str = "---To jest tekst, ktory zapisze do bazy danych-------To jest tekst, ktory zapisze do bazy danych----";
  for(int i=0; i<str.length(); ++i)
    pol->set(i, str[i]);
}

void test(IPolaczenie* pol) {
  cout << "[Adres polaczenia]: " << (long)pol << " [Tekst]: ";
  for(int i=0; i<pol->length(); ++i)
    cout << pol->get(i);
  cout<<endl;
}

int main() {

  const int ilosc = 10;
  IPolaczenie* polaczenia[ilosc];

  for(int i=0; i<ilosc; ++i)
    polaczenia[i] = Baza::getPolaczenie();


  inicjujBaze(polaczenia[0]);

  for(int i=0; i<ilosc; ++i) {
    cout<< i <<" : ";
    test(polaczenia[i]);
  }


  delete polaczenia[0];
  delete polaczenia[1];
  delete polaczenia[2];


  return 0;
}
