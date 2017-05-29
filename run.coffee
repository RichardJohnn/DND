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
character.emitter.on 'pickup', ->
  ShowScreen(randomLevel.blocks)
  term.drawImage(
    'https://s-media-cache-ak0.pinimg.com/originals/e7/f2/a7/e7f2a7b7e183ec6f3323f7f7680c0ce2.png',
    shrink:
      width:  term.width,
      height: term.height - 1
  )

DropHandler = _.partial(new DropHandler.instance, randomLevel)
character.emitter.on 'drop', DropHandler
character.emitter.on 'drop', -> ShowScreen(randomLevel.blocks)

