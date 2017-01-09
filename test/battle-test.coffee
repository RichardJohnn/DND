expect = require('chai').expect
Battle = require '../lib/battle/battle'
_ = require 'lodash'

describe 'Battle', ->
  beforeEach ->
    @entities = [
      {team:1, name:'Ghostface Killah'}
      {team:1, name:'Masta Killa'}
      {team:1, name:'GZA'}
      {team:2, name:'Tupac Shakur'}
      {team:2, name:'Suge Knight'}
      {team:2, name:'Yaki Kadafi'}
    ]

    sideIdentifier = 'team'
    @battle = new Battle(@entities, sideIdentifier)

  describe 'sides', ->
    it 'should group sides by a sideIdentifier', ->
      sides = @battle.sides()
      keys = Object.keys(sides)

      expect(keys.length).to.equal 2

  describe 'order', ->
    it 'should pick random order for which side fights', ->
      order = @battle.order()
      firstTeam  = order[0].team
      secondTeam = order[1].team
      thirdTeam  = order[2].team

      expect(firstTeam).to.not.equal secondTeam
      expect(firstTeam).to.equal     thirdTeam

    it 'reuse entities if they on a smaller side', ->
      @entities.pop()
      @entities.pop() #busting caps

      order = @battle.order()
      tupacs = _.filter order, {name: 'Tupac Shakur'}
      expect(tupacs.length).to.equal 3

      firstTeam  = order[0].team
      secondTeam = order[1].team
      thirdTeam  = order[2].team

  describe '#repeatTilLength', ->
    it 'repeats the array until the specified length is met', ->
      repeated = @battle.repeatTilLength([1,2,3], 5)
      expect(repeated).to.deep.equal [1,2,3,1,2]

    it 'lops off the end if the array is already longer than target length', ->
      repeated = @battle.repeatTilLength([1,2,3], 2)
      expect(repeated).to.deep.equal [1,2]

  describe '#step', ->
    it 'increments the actor pointer', ->
      startingPosition = @battle.pointer

      @battle.step()
      secondPosition = @battle.pointer
      expect(startingPosition).to.equal secondPosition - 1

      lengthOfActors   = @battle.length()
      _.times(lengthOfActors - 1, @battle.step)

      expect(startingPosition).to.equal secondPosition - 1

  describe "#run", ->
    it "initiates fighting until one team is dead or left or surrendered", ->
      @battle.run (result) =>
        expect(result).to.exist
        expect(result.winner).to.exist
        expect(result.cause).to.equal 'destruction'
        expect(@battle.done).to.be.true



