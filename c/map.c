#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

typedef struct Block {
  int type;
  bool walkable;
} Block;

typedef struct Map {
  int sizeX; 
  int sizeY; 
  int size;
  Block *blocks;
} Map;

Map makeMap(int sizeX, int sizeY) {
  Map m = { 
    .sizeX = sizeX, 
    .sizeY = sizeY, 
    .size = sizeX * sizeY,
  };
  m.blocks = malloc(m.size * sizeof(Block));
  for(int y = 0; y < m.sizeY; y++) {
    for(int x = 0; x < m.sizeX; x++) {
      int type = 0;
      if(x == 0) {
        type = 1;
      }
      m.blocks[y * m.sizeX + x].type = type;
    }
  }
  return m;
}

void drawMap(Map m) {
  for(int y = 0; y < m.sizeY; y++) {
    for(int x = 0; x < m.sizeX; x++) {
      Block b = m.blocks[y * m.sizeX + x];
      printf("%d ", b.type);
    }
    printf("\n");
  }
}
