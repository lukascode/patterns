#pragma once
#include "IPolaczenie.h"
#include "Polaczenie.h"

class Polaczenie;

class Baza {

public:

  static IPolaczenie* getPolaczenie();

private:

  static const int length = 100;
  char tab[length];

  Baza();
  static Baza* instance;
  static Baza* getInstance();



  friend class Polaczenie;

};
