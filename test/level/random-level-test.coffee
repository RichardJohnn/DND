expect = require('chai').expect
RandomLevel = require '../../lib/level/random-level'

describe 'RandomLevel', ->
  beforeEach ->
  describe 'size', ->
    it.only 'sizes', ->
      randomLevel = new RandomLevel({sizeX: 10, sizeY: 11})
      expect(randomLevel.w).to.equal 10
      expect(randomLevel.h).to.equal 11

