PF= require 'pathfinding'

class PathFinder
  makeGrid: (matrix) ->
    @grid = new PF.Grid(matrix)
