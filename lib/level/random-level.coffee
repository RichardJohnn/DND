container = require '../../deps'
Level = require './level'
Block = require './block'

randomBlock = ->
  block = new Block(Math.random() > .9)
  if Math.random() > .8
    item =
      color: if Math.random() > .5 then 10 else 9
      name: 'pizza'
    block.items = [item]
  block

module.exports = ({sizeX, sizeY}) ->
  level = new Level()
  level.w = sizeX
  level.h = sizeY

  level.blocks =
    ((randomBlock() for h in [0..level.h]) for w in[0..level.w])

  level

