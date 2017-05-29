#include <ncurses.h>
#include <unistd.h>
#include "map.c"

int main(int argc, char *argv[]){
  Map m = makeMap(10, 12);

  drawMap(m);
  //printf("%d, %d\n", m.sizeX, m.sizeY);

  return 0;
}
