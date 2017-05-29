module.exports = ({_}) ->
  ({blocks}, {source}) ->
    character = source

    {x, y, inventory} = character
    {items}           = blocks[x][y]

    if _.some inventory
      items.push(inventory.pop())
