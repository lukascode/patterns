#pragma once
#include <vector>
#include "IPolaczenie.h"
#include "Baza.h"

class Baza;

class Polaczenie : public IPolaczenie {

public:
  virtual char get(int indeks);

  virtual void set(int indeks, char c);

  virtual int length();

  virtual ~Polaczenie();

private:
  Polaczenie();

  Baza* baza;

  static int _iterator;
  static const int liczbaPolaczenMax = 3;
  static IPolaczenie* getInstance();
  static std::vector<Polaczenie*> polaczenia;


  friend class Baza;


};
