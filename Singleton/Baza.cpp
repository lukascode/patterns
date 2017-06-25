#include "Baza.h"


Baza* Baza::instance = nullptr;


Baza::Baza() {

}

IPolaczenie* Baza::getPolaczenie() {
  return Polaczenie::getInstance();
}

Baza* Baza::getInstance() {
    if(!instance) {
      instance = new Baza();
    }
    return instance;
}
