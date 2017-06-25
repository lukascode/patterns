#include "Polaczenie.h"

int Polaczenie::_iterator = -1;

std::vector<Polaczenie*> Polaczenie::polaczenia;

Polaczenie::Polaczenie() {
  baza = Baza::getInstance();
}

IPolaczenie* Polaczenie::getInstance() {
    if(polaczenia.size() == liczbaPolaczenMax) {
      if( _iterator == (polaczenia.size()-1) ) {
        _iterator = 0;
        return polaczenia[_iterator];
      } else return polaczenia[++_iterator];
    } else {
        polaczenia.push_back(new Polaczenie());
        return polaczenia[++_iterator];
    }
}

Polaczenie::~Polaczenie() {
  if(baza) delete baza;
}

char Polaczenie::get(int indeks) {
  return baza->tab[indeks];
}

void Polaczenie::set(int indeks, char c) {
  baza->tab[indeks] = c;
}

int Polaczenie::length() {
  return Baza::length;
}
