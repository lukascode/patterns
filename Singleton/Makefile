
CC = g++
CFLAGS = -std=c++11 -c -ggdb
EXEC = singleton
OBJS = main.o Baza.o Polaczenie.o

$(EXEC): $(OBJS)
		$(CC) $(OBJS) -o $@

%.o: %.cpp
		$(CC) $(CFLAGS) $<

Baza.o: Baza.h
Polaczenie.o: Polaczenie.h

clean:
		rm -f *.o
