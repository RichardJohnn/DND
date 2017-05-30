BGCOLOR = -1
COLOR   = -1

module.exports = (term, matrix) ->
  #term.clear()

  term.hideCursor()

  rowCount = matrix.length
  colCount = matrix[0].length

  matrix.map (row, x) ->
    row.map (block, y) ->
      return unless block.dirty
      block.dirty = false

      color   = if block.solid then 5 else 3
      char    = if block.solid then ' ' else '.'
      bgcolor = if block.solid then 100 else 222
#♥
      item = block.items[0]
      if item?
        char  = item.char  ? 'e'
        color = item.color ? 9

      character = block.inhabitants[0]
      if character?
        char  = character.char
        color = character.color

      term.color256(color)
      term.bgColor256(bgcolor)
      term.moveTo(x, y, char)

  term.moveTo(matrix.length, matrix[0].length)
  #term.bgColor256(0)
  term('\n')

