module.exports = ({_}) ->
  (level, {source}) ->
    {blocks}  = level
    character = source

    {x, y, inventory} = character
    {items}           = blocks[x][y]

    if _.some inventory
      items.push(inventory.pop())
      blocks[x][y].dirty = true

    level

