module.exports = ({_, Character}) ->
  console.log Character
  (level, {source}) ->
    character = source

    {items} = level
      .blocks[character.x][character.y]

    if _.some items
      console.log 'ITEMS'
      character.inventory.push(items.pop())
