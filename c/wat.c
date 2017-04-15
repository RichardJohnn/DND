#include <ncurses.h>
#include <unistd.h>

int main(int argc, char *argv[]){
  int x, y, max_x, max_y = 0;

  WINDOW *w = initscr();
  cbreak();
  keypad(stdscr, TRUE);
  nodelay(w, TRUE);
  noecho();
  curs_set(FALSE);

  getmaxyx(stdscr, max_y, max_x);

  x = max_x / 2;
  y = max_y / 2;

  clear();
  printf("x: %d, y: %d", x, y);

  char ch, lastCh;

  while(TRUE){
    clear();
    getmaxyx(stdscr, max_y, max_x);

    ch = getch();
    if (ch != -1)
      lastCh = ch;

    if (ch == 27) break;
    switch(ch){
      case 2:
        y++;
        break;
      case 3:
        y--;
        break;
      case 4:
        x--;
        break;
      case 5:
        x++;
        break;
      case -1:
        break;
      default:
        continue;
    }

    mvprintw(max_y - 1, 0, "%d", lastCh);
    mvprintw(y, x, "@");

    refresh();
    usleep(35000);
  }

  endwin();
  return 0;
}

int countStdin(){
  char ch[1];
  int n = 0;
  while(read(0, ch, 1) > 0){
    printf("%s", ch);
    n++;
  }

  return n-1;
}
