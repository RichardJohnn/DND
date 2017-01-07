expect      = require('chai').expect
worldReader = require '../../lib/worldEngineAdapter/reader'
_           = require 'lodash'

describe 'World Engine Reader', ->
  beforeEach ->
    @setTimeout = (timeout) -> @timeout = timeout
    @decoded = worldReader()

  describe 'reads the world engine protobuf file', ->
    it('should have biome information', (done) ->
      expect(@decoded.biome).to.exist
      done()
    ).timeout(6789)

