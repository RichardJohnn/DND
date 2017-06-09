expect = require('chai').expect
container = require '../../deps'
_ = require 'lodash'

describe 'Character', ->
  beforeEach ->
    @character  = new container.Character.instance
    @character.name = 'Taco'
    @character2 = new container.Character.instance
    @character2.name = 'Blaster'
    console.log @character, @character2

  describe "act", ->
    it "thinks and acts", ->
      @character.actions = ['attack']
      @character.foes = [@character2]
      action = @character.act()
      expect(action.target).to.eq @character2
      expect(action.action).to.eq 'attack'

  describe 'dead', ->
    it "tells you if a player has any life", ->
      expect(@character.dead()).to.be.true
      expect(@character2.dead()).to.be.true

      @character.hp = 1
      expect(@character.dead()).to.be.false
      expect(@character2.dead()).to.be.true

