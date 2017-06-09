{
  compose
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

term.on 'key', ( key , matches , data ) ->
  if key is 't'
    term
      .moveTo(blocks.length + 1, 0)
      .white('>>>')
      .inputField(
        #{ history: history , autoComplete: autoComplete , autoCompleteMenu: true } ,
        ( error, input ) ->
      )

ShowScreen(randomLevel)

MovementHandler = _.partial(new MovementHandler.instance, randomLevel)
character.emitter.on 'intent', compose(ShowScreen, MovementHandler)

PickupHandler = _.partial(new PickupHandler.instance, randomLevel)
character.emitter.on 'pickup', compose(ShowScreen, PickupHandler)

DropHandler = _.partial(new DropHandler.instance, randomLevel)
character.emitter.on 'drop', compose(ShowScreen, DropHandler)

#term.drawImage(
  #'http://www.asergeev.com/pictures/archives/2014/1438/jpeg/09.jpg',
  #shrink:
    #width:  term.width / 2,
    #height: term.height
#)
