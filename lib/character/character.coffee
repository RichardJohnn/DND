_ = require 'lodash'
{ EventEmitter } = require 'events'

class Character
  constructor: ->
    @name      = ''
    @char      = '@'
    @color     = 0
    @x  = 0
    @y  = 0
    @inventory = [char: 'Z']

    @strength     = 0
    @dexterity    = 0
    @intelligence = 0
    @constitution = 0
    @widsom       = 0
    @charisma     = 0

    @body = null

    @hp = 0

  emitter: new EventEmitter()

  emit: ->
    @emitter.emit(arguments...)

  dead: -> @hp <= 0

  die: ->
    @hp = 0
    @char = 'X'

  up: ->
    @emit 'intent',
      source:    this
      update:    => @y--

  down: ->
    @emit 'intent',
      source:    this
      update:    => @y++

  left: ->
    @emit 'intent',
      source:    this
      update:    => @x--

  right: ->
    @emit 'intent',
      source:    this
      update:    => @x++

  shit: ->

  pickup: (item) ->
    @emit 'pickup',
      source: this

  drop: (item) ->
    @emit 'drop',
      source: this

  act: ->
    action = {}

    if _.some(@foes)
      if 'attack' in @actions
        action.target = _.sample(@foes)
        action.action = 'attack'

    action


module.exports = Character
