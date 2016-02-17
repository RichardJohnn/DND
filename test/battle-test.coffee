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
  describe 'sides', ->
    it 'should group sides by a sideIdentifier', ->
      sideIdentifier = 'team'
      battle = new Battle(@entities, sideIdentifier)
      sides = battle.sides
      keys = Object.keys(sides)

      expect(keys.length).to.equal 2

  describe 'order', ->
    it 'should pick random order for which side fights', ->
      sideIdentifier = 'team'
      battle = new Battle(@entities, sideIdentifier)

      firstTeam  = battle.order[0].team
      secondTeam = battle.order[1].team
      thirdTeam  = battle.order[2].team

      expect(firstTeam).to.not.equal secondTeam
      expect(firstTeam).to.equal     thirdTeam

