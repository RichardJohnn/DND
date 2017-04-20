COMPILER = c99
CFLAGS = -g -std=c99 -Wall
LDFLAGS = -lncurses

.PHONY: clean

SRCS=$(wildcard *.c)

OBJS=$(SRCS:.c=.o)

all: $(OBJS)
	$(COMPILER) $(CFLAGS) wat.c -o wat $(LDFLAGS)

test: $(OBJS)
	$(COMPILER) $(CFLAGS) test.c -o test $(LDFLAGS)

clean:
	rm *.exe
	rm *.o
