BGCOLOR = -1
COLOR   = -1

module.exports = (term, level) ->
  #term.clear()

  {blocks} = level

  term.hideCursor()

  rowCount = blocks.length
  colCount = blocks[0].length

  blocks.map (row, x) ->
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

  term.moveTo(blocks.length, blocks[0].length)
  #term.bgColor256(0)
  term('\n')

  level

