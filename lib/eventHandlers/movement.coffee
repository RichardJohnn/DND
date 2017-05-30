module.exports = ({_}) ->
  (level, {source, update}) ->
    character = source

    currentBlock = level.blocks[character.x][character.y]
    _.pull(currentBlock.inhabitants, character)
    currentBlock.dirty = true

    update()

    currentBlock = level.blocks[character.x][character.y]
    currentBlock.inhabitants.push character
    currentBlock.dirty = true
