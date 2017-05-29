BGCOLOR = -1
COLOR   = -1

module.exports = (terminal, matrix) ->
  #terminal.clear()

  matrix.map (row, x) ->
    row.map (block, y) ->
      color   = if block.solid then 5 else 3
      char    = if block.solid then ' ' else '.'
      bgcolor = if block.solid then 100 else 222
#♥
      item = block.items[0]
      if item?
        char = item.char ? 'e'
        color = item.color ? 9

      character = block.inhabitants[0]
      if character?
        char = character.char
        color = character.color


      if color != COLOR
        COLOR = color
        terminal.color256(color)

      if BGCOLOR != bgcolor
        BGCOLOR = bgcolor
        terminal.bgColor256(bgcolor)

      terminal.moveTo(x, y, char)

