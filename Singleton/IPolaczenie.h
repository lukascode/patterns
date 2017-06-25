#pragma once

class IPolaczenie {

public:

  //Interface
  virtual char get(int indeks)=0;
  virtual void set(int indeks, char c)=0;
  virtual int length()=0;

};
