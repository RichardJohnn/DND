CFLAGS = -g -std=c99 -Wall
LDFLAGS = -lncurses

.PHONY: clean

SRCS=$(wildcard *.c)

OBJS=$(SRCS:.c=.o)

all: $(OBJS)
	gcc $(CFLAGS) wat.c -o wat $(LDFLAGS)

test: $(OBJS)
	cc99 $(CFLAGS) test.c -o test $(LDFLAGS)

clean:
	rm *.exe
	rm *.o
