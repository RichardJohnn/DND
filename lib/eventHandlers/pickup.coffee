module.exports = ({_}) ->
  (level, {source}) ->
    {blocks}  = level
    character = source

    {x, y, inventory} = character
    {items}           = blocks[x][y]

    if _.some items
      inventory.push(items.pop())
      blocks[x][y].dirty = true

    level
