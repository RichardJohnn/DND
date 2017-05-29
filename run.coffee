_  = require 'lodash'
{ Character, PickupHandler } = require './deps'

term = require('terminal-kit').terminal
term.grabInput( { mouse: 'button' , focus: true } )
term.applicationKeypad()

ShowScreen  = _.partial(require('./lib/gui/show-screen'), term)
RandomLevel = require './lib/level/random-level'

controls    = _.partial(require('./lib/controls/keyboard'), term)
character   = new Character.instance
controls(character)
character.x = 5
character.y = 20

randomLevel = new RandomLevel({sizeX: 80, sizeY: 40})

PickupHandler = _(new PickupHandler.instance)
  .bind(this)
  .partial(randomLevel)
  .value()

blocks = randomLevel.blocks
blocks[character.x][character.y].inhabitants.push character

character.emitter.on 'intent', (event) ->
  character = event.source
  currentBlock = randomLevel.blocks[character.x][character.y]
  _.pull(currentBlock.inhabitants, character)

  event.update()

  currentBlock = randomLevel.blocks[character.x][character.y]
  currentBlock.inhabitants.push character

  #ShowScreen(randomLevel.blocks)

character.emitter.on 'pickup', PickupHandler

character.emitter.on 'drop', ({source}) ->
  character = source
  currentBlock = randomLevel.blocks[character.x][character.y]
  {inventory} = character
  if _.some inventory
    currentBlock.items.push(inventory.pop())
    ShowScreen(randomLevel.blocks)


#ShowScreen(randomLevel.blocks)


