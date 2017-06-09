class Block
  constructor: (@solid) ->
    @weight = 0.0
    @dirty = true

    @inhabitants = []
    @items = []

module.exports = Block

