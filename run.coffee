{
  Character
  PickupHandler
  MovementHandler
  DropHandler
  _
} = require './deps'

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

blocks = randomLevel.blocks
blocks[character.x][character.y].inhabitants.push character

ShowScreen(randomLevel.blocks)

MovementHandler = _.partial(new MovementHandler.instance, randomLevel)
character.emitter.on 'intent', MovementHandler
character.emitter.on 'intent', -> ShowScreen(randomLevel.blocks)

PickupHandler = _.partial(new PickupHandler.instance, randomLevel)
character.emitter.on 'pickup', PickupHandler
character.emitter.on 'pickup', -> ShowScreen(randomLevel.blocks)

DropHandler = _.partial(new DropHandler.instance, randomLevel)
character.emitter.on 'drop', DropHandler
character.emitter.on 'drop', -> ShowScreen(randomLevel.blocks)

